package pers.ailurus.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.db.nosql.mongo.MongoDS;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static MongoDS mongoDS;
    private static MongoCollection<Document> collection;

    public static void init(String url, String port, String user, String password, String db, String coll) {
        Setting setting = new Setting();
        setting.set("host", String.format("%s:%s", url, port));
        setting.set("username", user);
        setting.set("password", password);
        mongoDS = new MongoDS(setting);
        collection = mongoDS.getCollection(db, coll);
    }

    public static void init(String url, String port, String db, String coll) {
        Setting setting = new Setting();
        setting.set("host", String.format("%s:%s", url, port));
        mongoDS = new MongoDS(setting);
        collection = mongoDS.getCollection(db, coll);
    }

    public static void insert(Dict dict) {
        collection.insertOne(Document.parse(JSONUtil.toJsonStr(dict)));
    }

    public static void insert(Dict[] dictArray) {
        List<Document> documents = new ArrayList<>();
        for (Dict dict : dictArray) {
            documents.add(Document.parse(JSONUtil.toJsonStr(dict)));
        }
        collection.insertMany(documents);
    }

    public static Dict findOne(String key, String value) {
        return Dict.parse(collection.find(new Document(key, value)).first().toJson());
    }

    public static List<Dict> findAll(String key, String value) {
        List<Dict> dictList = new ArrayList<>();
        for (Document document : collection.find(new Document(key, value))) {
            dictList.add(Dict.parse(document.toJson()));
        }
        return dictList;
    }
}
