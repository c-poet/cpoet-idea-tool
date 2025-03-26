package cn.cpoet.tool.httpclient;

import cn.cpoet.tool.util.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * 响应体处理器
 *
 * @author CPoet
 */
public class EntityResponseHandler<T> extends AbstractResponseHandler<T> {

    private final Class<T> entityClass;

    public EntityResponseHandler(@NotNull Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T handleEntity(HttpEntity httpEntity) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(httpEntity);
        return bytes == null || bytes.length == 0 ? null : JsonUtil.read(bytes, entityClass);
    }
}
