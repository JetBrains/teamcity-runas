/*
 * Copyright 2000-2022 JetBrains s.r.o.
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

import com.intellij.execution.ExecutionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class ConfigurationImplTest {
  @DataProvider(name = "parsePropFiles")
  public Object[][] getParsePropFilesCases() {
    return new Object[][] {
      {
        "key=val",
        new HashMap<Object, Object>() {{
          put("key", "val");
        }},
      },

      {
        "key=val\nname=abc",
        new HashMap<Object, Object>() {{
          put("key", "val");
          put("name", "abc");
        }},
      },

      {
        "",
        new HashMap<Object, Object>()
      },

      {
        "   ",
        new HashMap<Object, Object>()
      },

      {
        "   \n   \n",
        new HashMap<Object, Object>()
      },

      {
        "  key  =  val\n   name= abc   ",
        new HashMap<Object, Object>() {{
          put("key", "val");
          put("name", "abc");
        }},
      },

      {
        "\n\nkey=val\n\n\n\n\n   \nname=abc\n\n\n",
        new HashMap<Object, Object>() {{
          put("key", "val");
          put("name", "abc");
        }},
      },

      {
        "key=val=1=",
        new HashMap<Object, Object>() {{
          put("key", "val=1=");
        }},
      },

      {
        "key= \"val  \"",
        new HashMap<Object, Object>() {{
          put("key", "\"val  \"");
        }},
      },

      {
        " key = c:\\aa\\nn ",
        new HashMap<Object, Object>() {{
          put("key", "c:\\aa\\nn");
        }},
      },

      {
        "key=val\n# name=abc",
        new HashMap<Object, Object>() {{
          put("key", "val");
        }},
      },

      {
        "key=val\n name=#abc",
        new HashMap<Object, Object>() {{
          put("key", "val");
          put("name", "#abc");
        }},
      },

      {
        "# ### key=val\n name=#abc",
        new HashMap<Object, Object>() {{
          put("name", "#abc");
        }},
      },

      {
        "# ### key=val\n name=# abc#",
        new HashMap<Object, Object>() {{
          put("name", "# abc#");
        }},
      },
    };
  }

  @Test(dataProvider = "parsePropFiles")
  public void shouldParsePropFiles(
    @NotNull final String data,
    @Nullable HashMap<String, String> expectedValues) throws ExecutionException, IOException {
    // Given
    final Configuration config = createInstance();

    // When
    config.load(new ByteArrayInputStream(Charset.forName("UTF-8").encode(data).array()));

    // Then
    //noinspection ConstantConditions
    then(config.entrySet()).isEqualTo(expectedValues.entrySet());
  }

  @NotNull
  private Configuration createInstance()
  {
    return new ConfigurationImpl();
  }
}
