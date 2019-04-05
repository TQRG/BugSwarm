/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.ocpsoft.common.util.Assert;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.context.ContextBase;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.context.RewriteState;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.DefaultParameterStore;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.param.Parameterized;
import org.ocpsoft.rewrite.param.Transposition;
import org.ocpsoft.rewrite.util.ParameterUtils;
import org.ocpsoft.rewrite.util.Visitor;

/**
 * An {@link Operation} that allows for conditional evaluation of nested {@link Rule} sets.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Subset extends DefaultOperationBuilder implements CompositeOperation, Parameterized
{
   private static Logger log = Logger.getLogger(Subset.class);
   private final Configuration config;

   private Subset(Configuration config)
   {
      Assert.notNull(config, "Configuration must not be null.");
      this.config = config;
   }

   public static Subset evaluate(Configuration config)
   {
      return new Subset(config);
   }

   /*
    * Executors
    */
   @Override
   public void perform(Rewrite event, EvaluationContext context)
   {
      /*
       * Highly optimized loop - for performance reasons. Think before you change this!
       */
      List<Rule> rules = config.getRules();

      Rule rule = null;
      final EvaluationContextImpl subContext = new EvaluationContextImpl();
      for (int i = 0; i < rules.size(); i++)
      {
         rule = rules.get(i);
         event.getEvaluatedRules().add(rule);

         subContext.clear();
         subContext.put(ParameterStore.class, context.get(ParameterStore.class));
         ParameterValueStore values = (ParameterValueStore) context.get(ParameterValueStore.class);
         subContext.put(ParameterValueStore.class, values);
         subContext.setState(RewriteState.EVALUATING);

         if (rule.evaluate(event, subContext))
         {
            if (handleBindings(event, subContext, values))
            {
               subContext.setState(RewriteState.PERFORMING);

               if (log.isDebugEnabled())
                  log.debug("Rule [" + rule + "] matched and will be performed.");

               List<Operation> preOperations = subContext.getPreOperations();
               for (int k = 0; k < preOperations.size(); k++) {
                  preOperations.get(k).perform(event, subContext);
               }

               if (event.getFlow().isHandled())
               {
                  break;
               }

               rule.perform(event, subContext);

               if (event.getFlow().isHandled())
               {
                  break;
               }

               List<Operation> postOperations = subContext.getPostOperations();
               for (int k = 0; k < postOperations.size(); k++) {
                  postOperations.get(k).perform(event, subContext);
               }

               if (event.getFlow().isHandled())
               {
                  break;
               }
            }
         }
         else
         {
            event.getEvaluatedRules().remove(rule);
         }
      }
   }

   private boolean handleBindings(final Rewrite event, final EvaluationContextImpl context,
            ParameterValueStore values)
   {
      boolean result = true;
      ParameterStore store = (ParameterStore) context.get(ParameterStore.class);

      for (Entry<String, Parameter<?>> entry : store) {
         Parameter<?> parameter = entry.getValue();
         String value = values.retrieve(parameter);

         if (!ParameterUtils.enqueueSubmission(event, context, parameter, value))
         {
            result = false;
            break;
         }
      }
      return result;
   }

   /*
    * Getters
    */

   @Override
   public List<Operation> getOperations()
   {
      return Collections.emptyList();
   }

   class EvaluationContextImpl extends ContextBase implements EvaluationContext
   {
      private final List<Operation> preOperations = new ArrayList<Operation>();
      private final List<Operation> postOperations = new ArrayList<Operation>();
      private RewriteState state;

      public EvaluationContextImpl()
      {
         put(ParameterStore.class, new DefaultParameterStore());
      }

      @Override
      public void addPreOperation(final Operation operation)
      {
         this.preOperations.add(operation);
      }

      @Override
      public void addPostOperation(final Operation operation)
      {
         this.preOperations.add(operation);
      }

      /**
       * Get an immutable view of the added pre-{@link Operation} instances.
       */
      public List<Operation> getPreOperations()
      {
         return Collections.unmodifiableList(preOperations);
      }

      /**
       * Get an immutable view of the added post-{@link Operation} instances.
       */
      public List<Operation> getPostOperations()
      {
         return Collections.unmodifiableList(postOperations);
      }

      @Override
      public String toString()
      {
         return "EvaluationContextImpl [preOperations=" + preOperations + ", postOperations=" + postOperations + "]";
      }

      /**
       * Clears the state of this context so that it may be reused, saving instantiation cost during rule iteration.
       */
      @Override
      public void clear()
      {
         this.postOperations.clear();
         this.postOperations.clear();
         super.clear();
      }

      @Override
      public RewriteState getState()
      {
         return state;
      }

      public void setState(RewriteState state)
      {
         this.state = state;
      }
   }

   @Override
   public Set<String> getRequiredParameterNames()
   {
      return Collections.emptySet();
   }

   @Override
   public void setParameterStore(final ParameterStore parent)
   {
      for (final Rule rule : config.getRules()) {
         if (rule instanceof RuleBuilder) {
            ParameterizedCallback callback = new ParameterizedCallback() {
               @Override
               public void call(Parameterized parameterized)
               {
                  Set<String> names = parameterized.getRequiredParameterNames();
                  if (rule instanceof RuleBuilder)
                  {
                     ParameterStore store = ((RuleBuilder) rule).getParameterStore();

                     for (Entry<String, Parameter<?>> entry : parent) {
                        String name = entry.getKey();
                        Parameter<?> parentParam = entry.getValue();

                        if (!store.contains(name)) {
                           store.get(name, parentParam);
                        }
                        else
                        {
                           Parameter<?> parameter = store.get(name);
                           for (Binding binding : parameter.getBindings()) {
                              if (!parentParam.getBindings().contains(binding))
                                 throwRedefinitionError(rule, name);
                           }

                           for (Constraint<?> constraint : parameter.getConstraints()) {
                              if (!parentParam.getConstraints().contains(constraint))
                                 throwRedefinitionError(rule, name);
                           }

                           for (Transposition<?> transposition : parameter.getTranspositions()) {
                              if (!parentParam.getTranspositions().contains(transposition))
                                 throwRedefinitionError(rule, name);
                           }

                           if (parentParam.getConverter() != null
                                    && !parentParam.getConverter().equals(parameter.getConverter()))
                              throwRedefinitionError(rule, name);

                           if (parentParam.getValidator() != null
                                    && !parentParam.getValidator().equals(parameter.getValidator()))
                              throwRedefinitionError(rule, name);
                        }
                     }

                     for (String name : names) {
                        Parameter<?> parameter = store.get(name, new DefaultParameter(name));
                        if (parameter instanceof ConfigurableParameter<?>)
                           ((ConfigurableParameter<?>) parameter).bindsTo(Evaluation.property(name));
                     }
                     parameterized.setParameterStore(store);
                  }

               }

               private void throwRedefinitionError(Rule rule, String name)
               {
                  throw new IllegalStateException("Subset cannot re-configure parameter [" + name
                           + "] that was configured in parent Configuration. Re-definition was attempted at ["
                           + rule + "] ");
               }
            };

            Visitor<Condition> conditionVisitor = new ParameterizedConditionVisitor(callback);
            new ConditionVisit(rule).accept(conditionVisitor);

            Visitor<Operation> operationVisitor = new ParameterizedOperationVisitor(callback);
            new OperationVisit(rule).accept(operationVisitor);
         }
      }
   }

   @Override
   public String toString()
   {
      return "Subset.evaluate(" + config + ")";
   }
}
