

package jetbrains.buildServer.runAs.agent;

import org.jetbrains.annotations.NotNull;

public class NoModificationArgumentConverter implements Converter<String, String> {
  @NotNull
  @Override
  public String convert(@NotNull final String arg) {
    return arg;
  }
}