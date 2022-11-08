package site.chaotic.quantum.kookframework.struct.http;

import lombok.Data;

public class AssetCreateResponse extends BaseResponse<AssetCreateResponse.AssetCreate> {
    @Data
    public static class AssetCreate {
        private String url;
    }
}
