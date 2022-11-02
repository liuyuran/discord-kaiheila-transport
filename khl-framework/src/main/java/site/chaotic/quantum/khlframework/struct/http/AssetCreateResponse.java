package site.chaotic.quantum.khlframework.struct.http;

import lombok.Data;

public class AssetCreateResponse extends BaseResponse<AssetCreateResponse.AssetCreate> {
    @Data
    public static class AssetCreate {
        private String url;
    }
}
