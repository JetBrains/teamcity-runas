#pragma once
#include "IProcess.h"
#include "Settings.h"
#include "ExitCode.h"
#include "Result.h"
#include "Handle.h"
class Settings;

class ProcessAsUser: public IProcess
{		
	static Result<Environment> GetEnvironment(Handle& userToken, InheritanceMode inheritanceMode);

public:	
	virtual Result<ExitCode> Run(const Settings& settings, ProcessTracker& processTracker) const override;
};