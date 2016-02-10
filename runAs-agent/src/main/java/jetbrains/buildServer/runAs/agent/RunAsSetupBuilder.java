package jetbrains.buildServer.runAs.agent;

import com.intellij.openapi.util.text.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jetbrains.buildServer.dotNet.buildRunner.agent.*;
import jetbrains.buildServer.messages.serviceMessages.Message;
import jetbrains.buildServer.runAs.common.Constants;
import org.jetbrains.annotations.NotNull;

public class RunAsSetupBuilder implements CommandLineSetupBuilder {
  static final String TOOL_FILE_NAME = "runAs.cmd";
  static final String SETTINGS_EXT = ".runAs";
  static final String WARNING_STATUS = "WARNING";
  static final String CONFIG_FILE_CMD_KEY = "-c:";

  private static final String CONFIGURATION_PARAMETER_WAS_NOT_DEFINED_WARNING = "the configuration parameter \"%s\" was not defined or empty";
  private static final String RUN_AS_WAS_NOT_USED_MESSAGE = "RunAs was not used because %s";

  private final RunnerParametersService myParametersService;
  private final FileService myFileService;
  private final ResourcePublisher mySettingsPublisher;
  private final ResourceGenerator<CredentialsSettings> myCredentialsGenerator;
  private final ResourceGenerator<RunAsArgsSettings> myRunAsArgsGenerator;
  private final LoggerService myLoggerService;
  private final CommandLineArgumentsService myCommandLineArgumentsService;

  public RunAsSetupBuilder(
    @NotNull final RunnerParametersService parametersService,
    @NotNull final FileService fileService,
    @NotNull final ResourcePublisher settingsPublisher,
    @NotNull final ResourceGenerator<CredentialsSettings> credentialsGenerator,
    @NotNull final ResourceGenerator<RunAsArgsSettings> runAsArgsGenerator,
    @NotNull final LoggerService loggerService,
    @NotNull final CommandLineArgumentsService commandLineArgumentsService) {
    myParametersService = parametersService;
    myFileService = fileService;
    mySettingsPublisher = settingsPublisher;
    myCredentialsGenerator = credentialsGenerator;
    myRunAsArgsGenerator = runAsArgsGenerator;
    myLoggerService = loggerService;
    myCommandLineArgumentsService = commandLineArgumentsService;
  }

  @NotNull
  @Override
  public Iterable<CommandLineSetup> build(@NotNull final CommandLineSetup commandLineSetup) {
    if(!myParametersService.isRunningUnderWindows()) {
      return Collections.singleton(commandLineSetup);
    }

    final String user = myParametersService.tryGetConfigParameter(Constants.USER_VAR);
    if(StringUtil.isEmptyOrSpaces(user)) {
      sendWarning(String.format(CONFIGURATION_PARAMETER_WAS_NOT_DEFINED_WARNING, Constants.USER_VAR));
      return Collections.singleton(commandLineSetup);
    }

    final String password = myParametersService.tryGetConfigParameter(Constants.PASSWORD_VAR);
    if(StringUtil.isEmptyOrSpaces(password)) {
      sendWarning(String.format(CONFIGURATION_PARAMETER_WAS_NOT_DEFINED_WARNING, Constants.PASSWORD_VAR));
      return Collections.singleton(commandLineSetup);
    }

    final ArrayList<CommandLineResource> resources = new ArrayList<CommandLineResource>();
    resources.addAll(commandLineSetup.getResources());

    // Credentials
    final File credentialsFile = myFileService.getTempFileName(SETTINGS_EXT);
    final String credentialsContent = myCredentialsGenerator.create(new CredentialsSettings(user, password));
    resources.add(new CommandLineFile(mySettingsPublisher, credentialsFile, credentialsContent));

    // RunAs args
    final File runAsArgsFile = myFileService.getTempFileName(SETTINGS_EXT);
    List<CommandLineArgument> cmdLineArgs = new ArrayList<CommandLineArgument>();
    cmdLineArgs.add(new CommandLineArgument(commandLineSetup.getToolPath(), CommandLineArgument.Type.PARAMETER));
    cmdLineArgs.addAll(commandLineSetup.getArgs());
    final String commandLine = myCommandLineArgumentsService.createCommandLineString(cmdLineArgs);
    final String workingDirectory = myFileService.getCheckoutDirectory().getAbsolutePath();
    final String runAsArgsContent = myRunAsArgsGenerator.create(new RunAsArgsSettings(commandLine, workingDirectory));
    resources.add(new CommandLineFile(mySettingsPublisher, runAsArgsFile, runAsArgsContent));

    myLoggerService.onStandardOutput("Starting: " + commandLine);
    myLoggerService.onStandardOutput("in directory: " + workingDirectory);

    return Collections.singleton(
      new CommandLineSetup(
        getTool().getAbsolutePath(),
        Arrays.asList(
          new CommandLineArgument(CONFIG_FILE_CMD_KEY + credentialsFile.getAbsolutePath(), CommandLineArgument.Type.PARAMETER),
          new CommandLineArgument(CONFIG_FILE_CMD_KEY + runAsArgsFile.getAbsolutePath(), CommandLineArgument.Type.PARAMETER)),
        resources));
  }

  private void sendWarning(@NotNull final String reason) {
    myLoggerService.onMessage(new Message(String.format(RUN_AS_WAS_NOT_USED_MESSAGE, reason), WARNING_STATUS, null));
  }

  private File getTool() {
    final File path = new File(myParametersService.getToolPath(Constants.RUN_AS_TOOL_NAME), TOOL_FILE_NAME);
    myFileService.validatePath(path);
    return path;
  }
}