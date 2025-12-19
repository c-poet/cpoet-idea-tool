package cn.cpoet.tool.httpclient;

import cn.cpoet.tool.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpEntity;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author CPoet
 */
public class TypeRefResponseHandler<T> extends AbstractResponseHandler<T> {

    private final TypeReference<T> typeRef;

    public TypeRefResponseHandler(TypeReference<T> typeRef) {
        this.typeRef = typeRef;
    }

    @Override
    public T handleEntity(HttpEntity httpEntity) throws IOException {
        byte[] bytes = EntityUtils.toByteArray(httpEntity);
        return bytes == null || bytes.length == 0 ? null : JsonUtil.read(bytes, typeRef);
    }
}
