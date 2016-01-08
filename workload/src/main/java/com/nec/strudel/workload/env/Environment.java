package com.nec.strudel.workload.env;

public interface Environment {
    void start(ExecConfig conf);
    void stop(ExecConfig conf);
    void startSuite(ExecConfig conf);
    void stopSuite(ExecConfig conf);
}
