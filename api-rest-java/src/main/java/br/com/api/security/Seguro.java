package br.com.api.security;

import java.lang.annotation.*;
import javax.ws.rs.NameBinding;
import br.com.api.model.NivelDeAcesso;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Seguro {
    NivelDeAcesso[] value() default {};
}
