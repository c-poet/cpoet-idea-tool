package cn.cpoet.ideas.actions.patch.model;

import com.intellij.openapi.module.Module;

/**
 * @author CPoet
 */
public class GenPatchModule {

    private Module module;

    private boolean isApp;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean app) {
        isApp = app;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
