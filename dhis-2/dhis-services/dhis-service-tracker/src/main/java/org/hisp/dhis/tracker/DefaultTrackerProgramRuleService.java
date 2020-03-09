package org.hisp.dhis.tracker;

/*
 * Copyright (c) 2004-2020, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.programrule.engine.DefaultProgramRuleEngineService;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.domain.Enrollment;
import org.hisp.dhis.tracker.domain.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Enrico Colasante
 */
@Service
public class DefaultTrackerProgramRuleService
    implements TrackerProgramRuleService
{
    private final DefaultProgramRuleEngineService programRuleEngineService;

    public DefaultTrackerProgramRuleService( DefaultProgramRuleEngineService programRuleEngineService )
    {
        this.programRuleEngineService = programRuleEngineService;
    }

    @Override
    public Map<String, List<RuleEffect>> calculateEnrollmentRuleEffects( TrackerBundle trackerBundle )
    {
        return trackerBundle.getEnrollments()
            .stream()
            .collect( Collectors
                .toMap(
                    Enrollment::getEnrollment,
                    e -> programRuleEngineService
                        .evaluateEnrollment( e.getEnrollment() ) ) );
    }

    @Override
    public Map<String, List<RuleEffect>> calculateEventRuleEffects( TrackerBundle trackerBundle )
    {
        return trackerBundle.getEvents()
            .stream()
            .collect( Collectors
                .toMap(
                    Event::getEvent,
                    e -> programRuleEngineService.evaluateEvent( e.getEvent() ) ) );
    }
}