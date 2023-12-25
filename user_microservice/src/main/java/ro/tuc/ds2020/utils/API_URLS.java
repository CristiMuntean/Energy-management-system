package ro.tuc.ds2020.utils;

public enum API_URLS {
    DEVICE_MICROSERVICE(getUrlFromEnvironmentVariable());

    private static String getUrlFromEnvironmentVariable() {
        String ip = System.getenv("DEVICE_MICROSERVICE_IP");
        return ip == null || ip.isEmpty() ? "http://localhost:8082/api/" : "http://" + ip + ":8082/api/";
    }

    private final String url;

    API_URLS(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
