package cn.cpoet.tool.compatible;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author CPoet
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CompatibleItem {
    /**
     * 兼容的类
     */
    @XmlAttribute(name = "source", required = true)
    private String source;

    /**
     * 实现的类
     */
    @XmlAttribute(name = "impl")
    private String impl;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }
}
