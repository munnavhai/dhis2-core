package org.hisp.dhis.tracker.validation.hooks;

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

import static org.hisp.dhis.tracker.report.ValidationErrorReporter.newReport;
import static org.hisp.dhis.tracker.report.ValidationErrorReporter.newWarningReport;

import java.util.List;

import com.google.common.collect.Lists;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.tracker.TrackerImportStrategy;
import org.hisp.dhis.tracker.domain.Event;
import org.hisp.dhis.tracker.programrule.RuleActionValidator;
import org.hisp.dhis.tracker.report.TrackerErrorCode;
import org.hisp.dhis.tracker.report.ValidationErrorReporter;
import org.hisp.dhis.tracker.validation.TrackerImportValidationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Enrico Colasante
 */
@Component
public class EventRuleValidationHook
    extends AbstractTrackerDtoValidationHook
{
    private List<RuleActionValidator> validators;

    public EventRuleValidationHook( TrackedEntityAttributeService teAttrService )
    {
        super( Event.class, TrackerImportStrategy.CREATE_AND_UPDATE, teAttrService );
    }

    @Autowired( required = false )
    public void setValidators( List<RuleActionValidator> validators )
    {
        this.validators = validators;
    }

    @Override
    public void validateEvent( ValidationErrorReporter reporter, Event event )
    {
        TrackerImportValidationContext context = reporter.getValidationContext();

        validators
            .stream()
            .filter( v -> !v.isWarning() )
            .flatMap( v -> {
                List<String> errors = v.validateEvents( context.getBundle() ).get( event.getEvent() );
                return errors != null ? errors.stream() : Lists.newArrayList().stream();
            } )
            .forEach( e -> reporter.addError( newReport( TrackerErrorCode.E1200 ).addArg( e ) ) );

        validators
            .stream()
            .filter( v -> v.isWarning() )
            .flatMap( v -> {
                List<String> warnings = v.validateEvents( context.getBundle() ).get( event.getEvent() );
                return warnings != null ? warnings.stream() : Lists.newArrayList().stream();
            } )
            .forEach( e -> reporter.addWarning( newWarningReport( TrackerErrorCode.E1200 ).addArg( e ) ) );
    }
}