package net.absoft;

import org.testng.annotations.Factory;

public class DemmyTestFactory {
    @Factory
    public Object[] factory(){
return new Object[]{
  new AuthenticationServiceTest("one"),
        new AuthenticationServiceTest("two")
};
    }
}
