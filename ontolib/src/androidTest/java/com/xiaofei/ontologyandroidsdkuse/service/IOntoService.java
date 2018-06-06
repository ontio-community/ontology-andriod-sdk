package com.xiaofei.ontologyandroidsdkuse.service;

import com.xiaofei.ontologyandroidsdkuse.model.AppConfig;

import java.io.IOException;

public interface IOntoService {
    AppConfig getAppConfig() throws IOException;
}
