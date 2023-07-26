package com.lingh;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EspressoInlineExpressionParserTest {

    @Test
    void assertEvaluateForExpressionIsNull() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate(null);
        assertThat(expected, is(Collections.<String>emptyList()));
    }

    @Test
    void assertEvaluateForSimpleString() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate(" t_order_0, t_order_1 ");
        assertThat(expected.size(), is(2));
        assertThat(expected, hasItems("t_order_0", "t_order_1"));
    }

    @Test
    void assertEvaluateForNull() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_order_${null}");
        assertThat(expected.size(), is(1));
        assertThat(expected, hasItems("t_order_"));
    }

    @Test
    void assertEvaluateForLiteral() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_order_${'xx'}");
        assertThat(expected.size(), is(1));
        assertThat(expected, hasItems("t_order_xx"));
    }

    @Test
    void assertEvaluateForArray() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_order_${[0, 1, 2]},t_order_item_${[0, 2]}");
        assertThat(expected.size(), is(5));
        assertThat(expected, hasItems("t_order_0", "t_order_1", "t_order_2", "t_order_item_0", "t_order_item_2"));
    }

    @Test
    void assertEvaluateForRange() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_order_${0..2},t_order_item_${0..1}");
        assertThat(expected.size(), is(5));
        assertThat(expected, hasItems("t_order_0", "t_order_1", "t_order_2", "t_order_item_0", "t_order_item_1"));
    }

    @Test
    void assertEvaluateForComplex() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_${['new','old']}_order_${1..2}, t_config");
        assertThat(expected.size(), is(5));
        assertThat(expected, hasItems("t_new_order_1", "t_new_order_2", "t_old_order_1", "t_old_order_2", "t_config"));
    }

    @Test
    void assertEvaluateForCalculate() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_${[\"new${1+2}\",'old']}_order_${1..2}");
        assertThat(expected.size(), is(4));
        assertThat(expected, hasItems("t_new3_order_1", "t_new3_order_2", "t_old_order_1", "t_old_order_2"));
    }

    @Test
    void assertEvaluateForExpressionPlaceHolder() {
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate("t_$->{[\"new$->{1+2}\",'old']}_order_$->{1..2}");
        assertThat(expected.size(), is(4));
        assertThat(expected, hasItems("t_new3_order_1", "t_new3_order_2", "t_old_order_1", "t_old_order_2"));
    }

    @Test
    void assertEvaluateForLong() {
        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            expression.append("ds_");
            expression.append(i / 64);
            expression.append(".t_user_");
            expression.append(i);
            if (i != 1023) {
                expression.append(",");
            }
        }
        List<String> expected = new EspressoInlineExpressionParser().splitAndEvaluate(expression.toString());
        assertThat(expected.size(), is(1024));
        assertThat(expected, hasItems("ds_0.t_user_0", "ds_15.t_user_1023"));
    }

    @Test
    void assertHandlePlaceHolder() {
        assertThat(new EspressoInlineExpressionParser().handlePlaceHolder("t_$->{[\"new$->{1+2}\"]}"), is("t_${[\"new${1+2}\"]}"));
        assertThat(new EspressoInlineExpressionParser().handlePlaceHolder("t_${[\"new$->{1+2}\"]}"), is("t_${[\"new${1+2}\"]}"));
    }
}
