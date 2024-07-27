package com.mudra.scopedvalue;

import com.mudra.user.User;
import lombok.extern.slf4j.Slf4j;

/*
 * Rebinding of Scoped Value is allowed
 */
@Slf4j
public class ScopedValueRebindPlay {
    
    public static final ScopedValue<User> user = ScopedValue.newInstance();
    
    public static void main(String[] args) throws Exception {

        print("user is Bound => " + user.isBound());

        User bob = new User("bob");
        ScopedValue.runWhere(user, bob, ScopedValueRebindPlay::handleUser);
                        
        print("user is Bound => " + user.isBound());
    }
    
    private static void handleUser() {
        
        print("handleUser - " + user.get());
        
        ScopedValue.runWhere(user, new User("anonymous"), 
                ScopedValueRebindPlay::callAsAnonymous);  
        
        print("handleUser - " + user.get());
    }
    
    private static void callAsAnonymous() {
        print("callAsAnonymous - " + user.get());
   }

    public static void print(String m) {
        log.info("[{}] {}", Thread.currentThread().getName(), m);
    }
    
}
