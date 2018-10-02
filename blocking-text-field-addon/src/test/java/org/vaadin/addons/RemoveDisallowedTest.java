package org.vaadin.addons;

import org.junit.Test;
import org.vaadin.addons.client.BlockingUtils;

import static org.junit.Assert.assertEquals;

public class RemoveDisallowedTest {

    @Test
    public void testDisallowed() {
        String textGoingToBody = "abcd";
        String wantedChars = "abc1234fq";
        String res = BlockingUtils.removeDisallowedCharacters(textGoingToBody, wantedChars);
        assertEquals(res, "abc");
    }
}
