package otus.ms.social.service;

import liquibase.pro.packaged.T;

public interface  MessagePublisher<T extends Object> {
    void publish(T object);
}
