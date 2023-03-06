package pers.ailurus.utils;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class Config {

    @Alias("db_save")
    public boolean dbSave;

    public String url;
    public String port;
    public String database;
    public String collection;

    public String user;
    public String password;
}
