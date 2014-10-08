/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import com.sun.tools.javac.main.Main.Result;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Tests that automated fixes ("Did you mean...") are generated even when the -Xjcov compiler
 * option is not set.
 *
 * @author Eddie Aftandilian (eaftan@google.com)
 */
@RunWith(JUnit4.class)
public class EndPosTest {

  private PrintWriter printWriter;
  private ByteArrayOutputStream outputStream;
  ErrorProneTestCompiler compiler;

  @Before
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
    compiler = new ErrorProneTestCompiler.Builder()
        .named("test")
        .redirectOutputTo(printWriter)
        .build();
  }

  @Test
  public void fileWithError() throws Exception {
    Result exitCode = compiler.compile(compiler.fileManager().sources(getClass(),
        "bugpatterns/SelfAssignmentPositiveCases1.java"));
    outputStream.flush();
    assertThat("Compiler should have exited with ERROR status", exitCode, is(Result.ERROR));
    assertThat("Compiler error message should include suggested fix", outputStream.toString(),
        containsString("Did you mean 'this.a = b;'?"));
    assertThat("Compiler should not warn about WrappedTreeMap collisions", outputStream.toString(),
        not(containsString("WrappedTreeMap collision")));

  }
}
