package org.soak.mocha.plugin.mocha;

import org.soak.mocha.plugin.mocha.environment.MochaEnvironment;
import org.soak.mocha.plugin.mocha.environment.MochaEnvironmentSetup;

public interface MochaPlugin {

    void onSetup(MochaEnvironmentSetup preLaunch);

    void onStarting(MochaEnvironment launch);

}
