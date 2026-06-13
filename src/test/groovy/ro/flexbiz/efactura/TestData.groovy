package ro.flexbiz.efactura

public class TestData {
    public static String accessToken
    public static String taxId

    public static void init() {
        accessTokenInit()
    }

    private static void accessTokenInit() {
        taxId = "15487754"
        accessToken = "abc123"
    }
}