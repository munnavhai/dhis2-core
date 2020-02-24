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

import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.programrule.engine.DefaultProgramRuleEngineService;
import org.hisp.dhis.rules.models.RuleEffect;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.converter.TrackerConverterService;
import org.hisp.dhis.tracker.domain.Enrollment;
import org.hisp.dhis.tracker.domain.Event;
import org.hisp.dhis.tracker.preheat.TrackerPreheat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Enrico Colasante
 */
@Slf4j
@Service
public class DefaultTrackerProgramRuleService
    implements TrackerProgramRuleService
{
    private final DefaultProgramRuleEngineService programRuleEngineService;

    private final TrackerConverterService<Enrollment, ProgramInstance> enrollmentTrackerConverterService;

    private final TrackerConverterService<Event, ProgramStageInstance> eventTrackerConverterService;

    public DefaultTrackerProgramRuleService( DefaultProgramRuleEngineService programRuleEngineService,
        TrackerConverterService<Enrollment, ProgramInstance> enrollmentTrackerConverterService,
        TrackerConverterService<Event, ProgramStageInstance> eventTrackerConverterService )
    {
        this.programRuleEngineService = programRuleEngineService;
        this.enrollmentTrackerConverterService = enrollmentTrackerConverterService;
        this.eventTrackerConverterService = eventTrackerConverterService;
    }

    @Override
    public Map<Enrollment, List<RuleEffect>> calculateEnrollmentRuleEffects( TrackerBundle trackerBundle )
    {
        return getProgramRulesForEnrollments( trackerBundle.getEnrollments(), trackerBundle.getPreheat() );
    }

    @Override
    public Map<Event, List<RuleEffect>> calculateEventRuleEffects( TrackerBundle trackerBundle )
    {
        return getProgramRulesForEvents( trackerBundle.getEvents(), trackerBundle.getPreheat() );
    }

    private Map<Enrollment, List<RuleEffect>> getProgramRulesForEnrollments( List<Enrollment> enrollments,
        TrackerPreheat preheat )
    {
        return enrollments
            .parallelStream()
            .collect( Collectors
                .toMap(
                    Function.identity(),
                    e -> programRuleEngineService
                        .evaluateEnrollment( enrollmentTrackerConverterService.from( preheat, e ) ) ) );
    }

    private Map<Event, List<RuleEffect>> getProgramRulesForEvents( List<Event> events,
        TrackerPreheat preheat )
    {
        return events
            .parallelStream()
            .collect( Collectors
                .toMap(
                    Function.identity(),
                    e -> programRuleEngineService.evaluateEvent( eventTrackerConverterService.from( preheat, e ) ) ) );
    }
}