package com.testpoke.core.net;

import java.util.Arrays;

/*
 * Created by Jansel Valentin on 5/4/14.
 */
public enum StandardEndpoint implements Endpoint {
    TRUST{
        @Override
        public String endpoint() {
            return "https://api.testpoke.com/v2/access/tokens/trust";
        }
    },
    REVOKE{
        @Override
        public String endpoint() {
            return "https://api.testpoke.com/v2/access/tokens/revoke";
        }
    },
    SESSION{
        final String flatEndpoint = "https://api.testpoke.com/v2/apps/$@/builds/$@/sessions";
        String endpoint = "";

        @Override
        public String endpoint() {
            return endpoint;
        }

        public void parse(String...args){
            String[] params = flatEndpoint.split("@");
            for (int i = 0; i < params.length; ++i) {
                if(i == args.length)
                    continue;
                params[i] = params[i].trim();
                params[i] = params[i].replaceAll("\\$", args[i]);
            }
            endpoint = Arrays.asList(params).toString().replaceAll("^\\[|\\]$", "").replaceAll("(, |,| ,| , )", "");
        }
    };

    abstract String endpoint();

    public void parse(String...args){}
}
