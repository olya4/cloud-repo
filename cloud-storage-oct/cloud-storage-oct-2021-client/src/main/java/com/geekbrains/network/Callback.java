package com.geekbrains.network;

import com.geekbrains.model.AbstractMessage;

public interface Callback {

    void callback(AbstractMessage msg) throws Exception;

}
