package jetbrains.buildServer.runAs.agent;

import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineArgument;
import jetbrains.buildServer.runAs.common.LoggingLevel;
import jetbrains.buildServer.runAs.common.WindowsIntegrityLevel;
import org.jetbrains.annotations.NotNull;

public class UserCredentials {
  private final String myUser;
  private final String myPassword;
  private final WindowsIntegrityLevel myWindowsIntegrityLevel;
  private final LoggingLevel myLoggingLevel;
  private final List<CommandLineArgument> myAdditionalArgs;

  public UserCredentials(
    @NotNull final String user,
    @NotNull final String password,
    @NotNull final WindowsIntegrityLevel windowsIntegrityLevel,
    @NotNull final LoggingLevel loggingLevel,
    @NotNull final List<CommandLineArgument> additionalArgs) {
    myUser = user;
    myPassword = password;
    myWindowsIntegrityLevel = windowsIntegrityLevel;
    myLoggingLevel = loggingLevel;
    myAdditionalArgs = additionalArgs;
  }

  @NotNull
  String getUser() {
    return myUser;
  }

  @NotNull
  String getPassword() {
    return myPassword;
  }

  public WindowsIntegrityLevel getWindowsIntegrityLevel() {
    return myWindowsIntegrityLevel;
  }

  public LoggingLevel getLoggingLevel() {
    return myLoggingLevel;
  }

  @NotNull
  List<CommandLineArgument> getAdditionalArgs() {
    return myAdditionalArgs;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final UserCredentials that = (UserCredentials)o;

    if (!myUser.equals(that.myUser)) return false;
    if (!myPassword.equals(that.myPassword)) return false;
    if (myWindowsIntegrityLevel != that.myWindowsIntegrityLevel) return false;
    if (myLoggingLevel != that.myLoggingLevel) return false;
    return myAdditionalArgs.equals(that.myAdditionalArgs);

  }

  @Override
  public int hashCode() {
    int result = myUser.hashCode();
    result = 31 * result + myPassword.hashCode();
    result = 31 * result + myWindowsIntegrityLevel.hashCode();
    result = 31 * result + myLoggingLevel.hashCode();
    result = 31 * result + myAdditionalArgs.hashCode();
    return result;
  }
}
