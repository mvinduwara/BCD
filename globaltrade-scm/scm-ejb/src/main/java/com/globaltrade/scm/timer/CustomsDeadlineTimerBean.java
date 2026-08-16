package com.globaltrade.scm.timer;

import com.globaltrade.scm.entity.CustomsDocument;
import com.globaltrade.scm.service.AlertDispatchService;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;

import java.time.ZoneId;
import java.util.Date;

@Singleton
public class CustomsDeadlineTimerBean {

    private static final long ESCALATION_LEAD_HOURS = 24;

    @Resource
    private TimerService timerService;

    @EJB
    private AlertDispatchService alertDispatchService;

    public void scheduleDeadlineAlert(CustomsDocument document) {
        Date deadline = Date.from(document.getSubmissionDeadline()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        Date alertAt = new Date(deadline.getTime() - ESCALATION_LEAD_HOURS * 60 * 60 * 1000);
        if (alertAt.before(new Date())) {
            return;
        }
        TimerConfig config = new TimerConfig(document.getId(), true);
        timerService.createSingleActionTimer(alertAt, config);
    }

    @Timeout
    public void onDeadlineApproaching(Timer timer) {
        Long documentId = (Long) timer.getInfo();
        alertDispatchService.dispatchCustomsDeadlineAlert(documentId);
    }
}