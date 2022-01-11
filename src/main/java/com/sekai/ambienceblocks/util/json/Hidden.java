package com.sekai.ambienceblocks.util.json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//Hides field from json serialization
@Retention(RetentionPolicy.RUNTIME)
public @interface Hidden {
}
