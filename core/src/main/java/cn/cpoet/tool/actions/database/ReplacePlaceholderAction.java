package cn.cpoet.tool.actions.database;

import cn.cpoet.tool.util.EditorUtil;
import cn.cpoet.tool.util.I18nUtil;
import cn.cpoet.tool.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.database.actions.QueryActionBase;
import com.intellij.database.console.JdbcConsole;
import com.intellij.database.console.JdbcConsoleProvider;
import com.intellij.database.script.ScriptModel;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.containers.JBIterable;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 替换SQL中的占位符
 *
 * @author CPoet
 */
public class ReplacePlaceholderAction extends QueryActionBase {

    public ReplacePlaceholderAction() {
        Presentation presentation = getTemplatePresentation();
        presentation.setText(I18nUtil.td("actions.database.ReplacePlaceholderAction.title"));
        presentation.setDescription(I18nUtil.td("actions.database.ReplacePlaceholderAction.description"));
    }

    @Override
    protected boolean isConsoleRequired() {
        return false;
    }

    @Override
    protected void invokeImpl(@Nullable JdbcConsole jdbcConsole, @NotNull ScriptModel<?> scriptModel, @NotNull JdbcConsoleProvider.Info info) {
        Project project = jdbcConsole == null ? info.editor.getProject() : jdbcConsole.getProject();
        JBIterable<? extends ScriptModel.StatementIt<?>> statements = scriptModel.statements();
        for (ScriptModel.StatementIt<?> statement : statements) {
            JBIterable<? extends ScriptModel.ParamIt<?>> parameters = statement.parameters();
            if (parameters.isEmpty()) {
                continue;
            }
            ReplacePlaceholderInfo placeholderInfo = getReplacePlaceholderInfo(statement.text());
            if (MapUtils.isEmpty(placeholderInfo.getParams())) {
                continue;
            }
            Map<String, Object> sqlParams = placeholderInfo.getParams();
            EditorUtil.runWithCaret(info.editor, c -> WriteCommandAction.runWriteCommandAction(project, () -> {
                int index = 1;
                int offset = 0;
                for (ScriptModel.ParamIt<?> parameter : parameters) {
                    String name;
                    if (!parameter.name().startsWith("#") && sqlParams.containsKey(parameter.name())) {
                        name = parameter.name();
                    } else {
                        name = "#" + index++;
                        if (!sqlParams.containsKey(name)) {
                            continue;
                        }
                    }
                    Object obj = sqlParams.get(name);
                    TextRange range = parameter.range();
                    String text = obj == null ? "NULL" : obj instanceof Number ? String.valueOf(obj) : "'" + obj + "'";
                    info.editor.getDocument().replaceString(offset + range.getStartOffset(), offset + range.getEndOffset(), text);
                    offset = offset + text.length() - range.getEndOffset() + range.getStartOffset();
                }
                offset = statement.range().getStartOffset() + offset;
                info.editor.getDocument().deleteString(offset + placeholderInfo.getStartIndex(), offset + placeholderInfo.getEndIndex() + 1);
            }));
        }
    }

    protected ReplacePlaceholderInfo getReplacePlaceholderInfo(String text) {
        int flag = 0;
        int i = text.length() - 1;
        StringBuilder sb = new StringBuilder();
        ReplacePlaceholderInfo info = new ReplacePlaceholderInfo();
        while (i >= 0) {
            char c = text.charAt(i);
            if (c == ']' && flag == 0) {
                flag = 1;
                sb.append(c);
                info.setEndIndex(i);
            } else if (c == '[' && (flag & 1) == 1 && (flag & (1 << 2)) != (1 << 2)) {
                sb.append(c);
                info.setStartIndex(i);
                break;
            } else if (c == '}' && flag == 0) {
                flag = 1 << 1;
                sb.append(c);
                info.setEndIndex(i);
            } else if (c == '{' && (flag & 1 << 1) == 1 << 1 && (flag & (1 << 2)) != (1 << 2)) {
                sb.append(c);
                info.setStartIndex(i);
                break;
            } else if (((flag & 1) == 1 || (flag & 1 << 1) == 1 << 1) && (c == '"' || c == '\'')) {
                if ((flag & 1 << 2) == 1 << 2) {
                    flag = flag ^ (1 << 2);
                } else {
                    flag = flag | (1 << 2);
                }
                sb.append(c == '\'' ? '"' : c);
            } else {
                sb.append(c);
            }
            --i;
        }
        if (flag == 1) {
            List<Object> params = JsonUtil.read(sb.reverse().toString(), new TypeReference<>() {
            });
            Map<String, Object> paramMap = new HashMap<>(params.size());
            int index = 1;
            for (Object param : params) {
                paramMap.put("#" + index++, param);
            }
            info.setParams(paramMap);
        }
        if (flag == 2) {
            info.setParams(JsonUtil.read(sb.reverse().toString(), new TypeReference<>() {
            }));
        }
        return info;
    }
}
