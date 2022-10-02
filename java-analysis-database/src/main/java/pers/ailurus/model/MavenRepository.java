package pers.ailurus.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MavenRepository {
    private String md5;

    private String name;

    private String version;

    private String url;

    private Long size;

    public String toCSVLine() {
        return String.format("%s,%s,%s,%s,%d", md5, name, version, url, size);
    }
}