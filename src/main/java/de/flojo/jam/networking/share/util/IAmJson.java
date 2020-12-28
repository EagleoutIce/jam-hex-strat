package de.flojo.jam.networking.share.util;

import java.io.Serializable;

import de.flojo.jam.networking.NetworkGson;

public interface IAmJson extends Serializable {
	default String toJson() {
		return NetworkGson.gson().toJson(this);
	}
}