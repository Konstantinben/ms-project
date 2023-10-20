package otus.ms.social.configuration.jdbc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ReadOnlyRepository {
}
