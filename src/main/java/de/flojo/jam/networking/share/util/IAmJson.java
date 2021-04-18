package de.flojo.jam.networking.share.util;

import de.flojo.jam.networking.NetworkGson;

import java.io.Serializable;

public interface IAmJson extends Serializable {
    default String toJson() {
        return NetworkGson.gson().toJson(this);
    }
}