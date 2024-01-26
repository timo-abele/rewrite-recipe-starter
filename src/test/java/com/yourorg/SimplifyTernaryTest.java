/*
 * Copyright 2024 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yourorg;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class SimplifyTernaryTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.parser(JavaParser.fromJavaVersion().classpath("guava"))
          .recipe(new SimplifyTernaryRecipes());
    }

    @Test
    void simplified() {
        rewriteRun(
          //language=java
          java(
            """
              class Test {
                  boolean trueCondition1 = true ? true : false;
              }
              """,
            """
              import com.google.common.primitives.Booleans;
              
              class Test {
                  boolean trueCondition1 = Booleans.contains(new boolean[]{true}, true);                  
              }
              """
          )
        );
    }

    @Test
    void unchanged() {
        rewriteRun(
          spec -> spec.recipe(new SimplifyTernaryRecipes()),
          //language=java
          java(
            """
              class Test {
                  boolean unchanged1 = booleanExpression() ? booleanExpression() : !booleanExpression();
                  boolean unchanged2 = booleanExpression() ? true : !booleanExpression();
                  boolean unchanged3 = booleanExpression() ? booleanExpression() : false;
                  
                  boolean booleanExpression() {
                    return true;
                  }
              }
              """
          )
        );
    }
}