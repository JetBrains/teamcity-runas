package jetbrains.buildServer.runAs.agent;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class LinuxArgumentConverterTest {

  @DataProvider(name = "convertCases")
  public Object[][] getConvertCases() {
    return new Object[][] {
      { "a b", "$'a b'" },
      { "", "$''" },
      { " ", "$' '" },
      { "a \\b", "$'a \\\\b'" },
      { "a 'b", "$'a \\'b'" },
      { "a \\b'", "$'a \\\\b\\''" },
    };
  }

  @Test(dataProvider = "convertCases")
  public void shouldConvert(@NotNull final String arg, @NotNull final String expectedConvertedArf) {
    // Given
    final Converter<String, String> instance = createInstance();

    // When
    final String actualConvertedArf = instance.convert(arg);

    // Then
    then(actualConvertedArf ).isEqualTo(expectedConvertedArf);
  }

  @NotNull
  private Converter<String, String> createInstance()
  {
    return new LinuxArgumentConverter();
  }
}
