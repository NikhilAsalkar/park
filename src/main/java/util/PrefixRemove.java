package util;

public class PrefixRemove {

        public String removePrefix(String s, String prefix)
        {
            if (s != null && prefix != null && s.startsWith(prefix)){
                return s.substring(prefix.length());
            }
            return s;
        }

    }
