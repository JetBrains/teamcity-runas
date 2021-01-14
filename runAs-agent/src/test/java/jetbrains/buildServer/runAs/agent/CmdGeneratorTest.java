/*
 * Copyright 2000-2021 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.runAs.agent;

import java.util.Arrays;
import java.util.Collections;
import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineArgument;
import jetbrains.buildServer.dotNet.buildRunner.agent.ResourceGenerator;
import org.jetbrains.annotations.NotNull;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class CmdGeneratorTest {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");
  private Mockery myCtx;
  private Converter<String, String> myArgumentConverter;

  @BeforeMethod
  public void setUp()
  {
    myCtx = new Mockery();
    //noinspection unchecked
    myArgumentConverter = (Converter<String, String>)myCtx.mock(Converter.class);
  }

  @DataProvider(name = "cmdLinesCases")
  public Object[][] getCmdLinesCases() {
    return new Object[][] {
      {
        new RunAsParams(
          Arrays.asList(
            new CommandLineArgument("tool", CommandLineArgument.Type.TOOL),
            new CommandLineArgument("a b", CommandLineArgument.Type.PARAMETER))),
      "'tool' 'a b'"},

      // empty args
      {
        new RunAsParams(
          Collections.<CommandLineArgument>emptyList()),
          ""},
    };
  }

  @Test(dataProvider = "cmdLinesCases")
  public void shouldGenerateContent(@NotNull final RunAsParams runAsParams, @NotNull final String expectedCmdLine) {
    // Given
    final ResourceGenerator<RunAsParams> instance = createInstance();
    myCtx.checking(new Expectations() {{
      allowing(myArgumentConverter).convert(with(any(String.class)));
      will(new CustomAction("convert") {
        @Override
        public Object invoke(final Invocation invocation) throws Throwable {
          return "'" + invocation.getParameter(0) + "'";
        }
      });
    }});

    // When
    final String content = instance.create(runAsParams);

    // Then
    then(content).isEqualTo("@ECHO OFF"
                            + LINE_SEPARATOR + expectedCmdLine
                            + LINE_SEPARATOR + "SET \"EXIT_CODE=%ERRORLEVEL%\""
                            + LINE_SEPARATOR + "EXIT /B %EXIT_CODE%");
  }

  @NotNull
  private ResourceGenerator<RunAsParams> createInstance()
  {
    return new CmdGenerator(myArgumentConverter);
  }
}