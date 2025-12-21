package cn.cpoet.tool.compatible;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * 兼容信息
 *
 * @author CPoet
 */
@XmlRootElement(name = "compatible")
@XmlAccessorType(XmlAccessType.FIELD)
public class Compatible {

    /**
     * 指定所在包名
     */
    @XmlElement(name = "package", required = true)
    private String packageName;

    /**
     * 最小兼容版本
     */
    @XmlElement(name = "since", required = true)
    private String since;

    /**
     * 兼容实现列表
     */
    @XmlElement(name = "item")
    @XmlElementWrapper(name = "items")
    private List<CompatibleItem> items;


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public List<CompatibleItem> getItems() {
        return items;
    }

    public void setItems(List<CompatibleItem> items) {
        this.items = items;
    }
}
