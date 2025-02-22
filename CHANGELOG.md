# Changelog

## 0.6.2
See [migration guide](https://fierycod.github.io/holy-lambda/#/migration-guide)

- [bb tasks] Introduce `bb hl:update-bb-tasks` as an automation for updating to stable version of tasks.
- [bb layer] Update babashka runtime to version `0.6.2`. Runtime does not precalculates the classpath. Cold start should be lower now! :) 
- [bb layer] Use `bb.edn` for specifying dependencies for babashka code instead of relying on `deps.edn`.
- [bb layer] Remove `babashka-shim` from the runtime, remove direct dependency on `holy-lambda`. 
  Users should put the newest `holy-lambda` version in `bb.edn` and use `bb hl:babashka:sync` to download the dependencies.
  Pack dependencies in the layer and reference it's ARN in `template.yml`.
  
  **Example**
  ```
  AWSTemplateFormatVersion: '2010-09-09'
  Transform: AWS::Serverless-2016-10-31
  Description: >
    Example basic lambda using `holy-lambda` micro library

  Parameters:
    Runtime:
      Type: String
      Default: provided
    Timeout:
      Type: Number
      Default: 40
    MemorySize:
      Type: Number
      Default: 128
    HL_ENTRYPOINT:
      Type: String
      Default: com.company.example-lambda.core

  Globals:
    Function:
      Runtime: !Ref Runtime
      Timeout: !Ref Timeout
      MemorySize: !Ref MemorySize
      Environment:
        Variables:
          HL_ENTRYPOINT: !Ref HL_ENTRYPOINT

  Resources:
    BabashkaDepsLayer:
      Type: AWS::Serverless::LayerVersion
      Properties:
        LayerName: BabashkaDepsLayer
        ContentUri: ./.holy-lambda/bb-clj-deps

    ExampleLambdaFunction:
      Type: AWS::Serverless::Function
      Properties:
        FunctionName: ExampleLambdaFunction
        Handler: com.company.example-lambda.core.ExampleLambda
        CodeUri: src
        Events:
          HelloEvent:
            Type: HttpApi
            Properties:
              ApiId: !Ref ServerlessHttpApi
              Path: /
              Method: GET
        Layers:
          - <HERE_PUT_PHISICAL_ID>
          - !Ref BabashkaDepsLayer 
   ...
  ```
## 0.6.1
- [bb layer] Update babashka to 0.6.3
- [holy-lambda] Parse inner body to `:body-parsed` (see https://github.com/FieryCod/holy-lambda/issues/74)

## 0.6.0 
- [holy-lambda] Improved performance of the runtime
- [bb layer] Renamed the `Entrypoint` environment variable for Babashka runtimes to `HL_ENTRYPOINT`.
- [bb tasks] Rename `hl:sync` to `hl:babashka:sync`. Sync downloads only `pods` and Babashka deps. Dependencies for babashka should be specified in bb.edn.
- [bb tasks] Remove `:mvn/local-repo` from `deps.edn`. Dependencies for Clojure & Native backend are not downloaded to a local directory. 
- [bb tasks] Project compilation happens without Docker now.
- [bb tasks] HL reverts allowance for not specifying the backend name. 
- [bb tasks] Rename `:runtime` in `bb.edn` to `:backend` to match the documentation. 
- [bb tasks] Remove `:entrypoint` and `:clj-alias` support. Use `:compile-cmd` in `:build` instead!
- [docker] Holy Lambda images are now available at ghcr. Please use the new `ghcr.io/fierycod/holy-lambda-builder` instead of `fierycod/graalvm-native-image`. Images don't include `aws`, `aws sam`, and `clojure`.
- [holy-lambda] Fix `application/json` content type detection
- [docker] Holy Lambda images are now available at ghcr. Please use the new `ghcr.io/fierycod/holy-lambda-builder` instead of `fierycod/graalvm-native-image`. Images don't include `aws`, `aws sam`, and `clojure`. 
  See here(https://github.com/FieryCod/holy-lambda/pkgs/container/holy-lambda-builder/versions)
- [docker] Holy Lambda now supports aarch64 for all of the runtimes and is now compatible with MacOS M1 :) 
- [docs] Documentation has been updated to match the newest version
- [babashka] [Old](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime) babashka artifact has been deprecated. 
  
  **Use one of**:
  - https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-arm64
  - https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-amd64
  
- [babashka layer] Bump babashka to 0.6.2
- [graalvm] Holy Lambda distributes configuration for GraalVM native-image to let the users use HL without `bb tasks`.
- [runtime] Fix `:awsRequestId` ctx property to point to runtime `:invocation-id`.
- [native:conf] Remove warning:
  ```
  WARNING: Could not resolve fierycod.holy_lambda.core$entrypoint$fn__6578 for reflection configuration. Reason: java.lang.ClassNotFoundException: fierycod.holy_lambda.core$entrypoint$fn__6578.
  ```

## 0.5.0 

**Rationale**
https://github.com/FieryCod/holy-lambda/discussions/68

- [bb tasks] Remove some of the bb tasks:
  - bucket:create
  - bucket:remove
  - stack:invoke 
  - stack:api
  - stack:pack
  - stack:deploy 
  - stack:describe
  - stack:destroy
  - stack:logs
- [holy-lambda] Renamed tasks:
  - bb hl:sync (renamed from bb stack:sync)
  - bb hl:compile (renamed from bb stack:compile)
  - bb hl:native:conf (renamed from bb native:conf)
  - bb hl:native:executable (renamed from bb native:executable)
  - bb hl:clean (renamed from bb stack:purge)
  - bb hl:version (renamed from bb stack:version)
  - bb hl:doctor (renamed from bb stack:doctor)
- [holy-lambda] Performance improvements
- [holy-lambda] Remove deflambda macro, merge mixins. Use plain `defn` for lambda definition.
  Holy Lambda exposes a single macro from the core namespace `entrypoint`. native-runtime namespace has been renamed to 
  custom-runtime. custom-runtime namespace is internal, not for public consumption.
- [holy-lambda] Remove interceptors support. For API's user can easily integrate Holy Lambda with ring or pedestal. The adapters for the mentioned libraries will be published soon.
- [docker] New compatible `fierycod/graalvm-native-image` images:
  - :ce - GraalVM CE 21.2.0
  - :dev - GraalVM DEV 21.3.0-dev-20210910_2147
- [bb layer] Layer version matches the holy-lambda version. New release 0.5.1 includes the latest babashka 0.6.1. Layer should be deployed via serverless repository deploy button:
  https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime
- Fix Clojure tools warnings, about not existing `.jar` file
- Don't re-download HL tasks in CI environments
- New documentation

## 0.2.3 (14-07-2021)
- [bb tasks] Add support for passing arguments to commands in `--` style.

  **Example**
  ```
  bb stack:invoke --validation-fn '(fn [response] true)'
  ```
  
- [ci] Run integration tests on all supported GraalVM images
- [bb tasks] Add experimental support for setting some of HL options via AWS variables. This should ease pain of using HL with AWS vault.
- [docker] Add `fierycod/graalvm-native-image:dev` which targets GraalVM CE dev releases :)
- [docker] Add zip program for CI based deployments where tasks are run in Docker context
- [bb tasks] Remove clj-kondo from the required commands
- [holy-lambda] Interceptors should not throw when response is byte encoded
- [holy-lambda] Don't use -H:+AllowIncompleteClasspath on behalf of user
- [holy-lambda] Use `DISABLE_SIGNAL_HANDLERS="true"` in bootstrap file
- [holy-lambda] Allow users to support arbitrary response if the response is byte encoded. Opens a way to support transit and other content-types.
- [holy-lambda] Properly parse response. Add support for AWS Step Functions.
- [holy-lambda] Use config-merge-dir instead of config-output-dir when executing bb native:conf. User should be allowed to specify custom configuration in `reflect-config.json`, `resource-config.json` that should not be overriden.
- [holy-lambda] Add support for sending a base64 encoded images from HL `hr/png-image`

## 0.2.2 (01-07-2021)
- [docs] Add documentation about using GraalVM-EE 
- [examples] Add basic cdk example
- [holy-lambda] System/exit 1 when handler is not found during `native:conf`
- [bb tasks] Don't remove empty `.aws` folder since tasks might not have proper permissions set, bind `~/.aws` folder at `/.aws` path instead.
- [bb tasks] Add support for CI deployments. 
- [bb tasks] Add support for docker `:network` option
- [docs] Add FAQ, CI support documents
- [bb tasks] Fix using docker in CI environment
- [bb tasks] Remove broken stack:lint command. Use plain clj-kondo instead
- [bb layer] Add a type hint & fix bb layer. Users should update to :runtime:version 0.1.2
- [CI] Add full test suite for basic lambda examples
- [bb tasks] Add support for multi environment deployments
- [bb tasks] Add `:validation-fn` for `bb stack:invoke` command:
 
  **Example**:

  ```
    bb stack:invoke :validation-fn '(fn [{:keys [body headers statusCode]}] (= body "Hello world"))'
  ```

## 0.2.1 (10-06-2021)

- [bb layer] Bump babashka version to 0.4.6
- [bb tasks] Add tail mode for `bb stack:logs`
- [bb tasks] Ignore unnecessary reflections and resources from native-configuration. Based on @borkdude `refl` project
- [bb tasks] Deprecate `docker:build:ee`. GraalVM EE support will arrive soon.
- [holy-lambda] Remove additional `LinkOption` reflection.

## 0.2.0 (10-06-2021)

- [bb tasks] Add additional describe:stacks step for stack:destroy, since sometimes stack cannot be destroyed
- [bb tasks] Allow to force compilation on sources
- [holy-lambda] Unify multiHeaders, headers conversion for both local and AWS environment. Breaking change if you were getting the headers from request.
- [holy-lambda-template] Add HttpApi reference in outputs for easier deployment
- [bb tasks] Fix self-manage-layers?
- [bb layer] Bump babashka version to 0.4.5
- [holy-lambda-template] Replace "." in stack name to "-"
- [Dockerfile CE] Bump ClojureTools. Minimize docker image

## 0.1.54 (06-06-2021)

- [bb tasks] Fix adding Clojure/Pods deps for Babashka projects. For now all the runtimes will create a temporary template-modified.yml file which should be ignored.
- [bb layer] 0.0.34 Fix doubled holy-lambda dependency. Update layer to use the latest holy-lambda library. 
- [bb layer] Fix edge case when babashka layer is published without the `bb` executable.
- [holy-lambda] Add html response support
- [holy-lambda] Automatically parse :body json string to PersistentMap
- [bb tasks] Check if aws command is available. If not then exit early!
- [bb tasks] Fix `CLJ_ALIAS` checking
- [readme,docs] Refine docs. Add documentation on using local libraries
- [holy-lambda-template] Apply correct formatting, use up to 6 characters for sha to prevent gen of incorrect bucket names

## 0.1.50 (03-06-2021)

- [bb tasks] Remove :build-variant. Replaced by [:docker](https://github.com/FieryCod/holy-lambda/blob/master/modules/holy-lambda-template/resources/leiningen/new/holy_lambda/bb.edn#L15) section
- [bb tasks] Change native-configurations path. User should be able to modify the configuration which is now generated at resources/native-configurations
- [bb tasks] Allow runtime override when using :native runtime. You can now invoke lambda in :java runtime when :native runtime is declared in bb.edn:
 
  **Example**:

  ```
  bb stack:invoke :runtime :java
  ```

- [bb tasks] Allow nil value for :native-deps option
- [bb tasks] Add support for setting :docker:volumes. This is crucial when working with local libraries, since HL runs tasks in docker context!
- [bb layer] Bump layer version to 0.0.32 which includes babashka v0.4.4
- [bb tasks] Commands bb bucket:create and bb bucket:remove can now create/remove specified bucket (not only the stack bucket) when :name parameter is used
- [bb tasks] Inform the user when docker image is not downloaded. This makes HL more user friendly.
- [bb tasks] Don't AOT whole project when uberjaring. AOT compile only the main class.
- [bb tasks] Add support for deps.edn aliases in bb.edn. Useful for local libraries

## 0.1.49 (21-05-2021)

- [bb tasks] Fix bb stack:sync which was failing on the first run
- [bb tasks] Add support for the custom profile in :infra
- Update babashka layer to version 0.4.1
- [bb tasks] Add an automated way of managing layer (update/downgrade). The new way is not compatible with the previous one. Please remove your old babashka layers stack and run `bb stack:sync`.
- [docs] Add documentation for getting started guide, refine template (entirely done by @lowecg. Thank you so much!)
- [holy-lambda] Change the order of execution for :leave interceptors. Order is the same as for Pedestal Interceptors now :)
- [bb tasks] Add validation for docker, AWS setup. Now holy-lambda will fail early with the helpful message if either AWS or docker is not properly set up.
- [bb tasks] Region is now derived from the profile if exists. A region in bb.edn overrides default region
- [bb tasks] Commands which depend on stack:sync are now restricted if the project is not synced
- [bb tasks] stack:invoke, stack:deploy, stack:api now support setting :params for AWS SAM parameters-overrides.

  **Example**:
  ```
  bb stack:invoke :params "{:SomeParameter \"Hello World\"}"
  ```

## 0.1.45 (05-05-2021)

- [docker] ce tag is used internally by bb tasks. Projects which use Makefiles should either switch to bb tasks or use image without tag
- [bb tasks] introduce bb tasks to manage stack, infrastracture and runtimes
- [bb layer] don't require uberjar. Provide sources as is.
- [holy-lambda] Fixup runtime error handling
- [holy-lambda] Fixup native-configuration gen
- [holy-lambda-template] Drop support for Makefile and Leiningen 
- [holy-lambda-babashka-shim] Provide a shim for jsonista and AWS interfaces so that babashka runtime works flawlessly

## 0.1.21 (21-04-2021)

- Add new docker tags: 
  - ce - for community edition
  - ce-ci - suitable for usage in docker to deploy with sam. Changes in Makefile are in progress
  - ee - you have to provide artifacts and build it on your own using Dockerfile provided in docker/ee
- Speedup interceptors on java/native runtime
- Provide Babashka runtime. Semantics of holy-lambda stay the same. For now bb is included in a zip which is totally lame. If more people will like this runtime then I will provide: babashka as a layer, minimal holy-lambda jar without jsonista (I would rather babashka to use jsonista instead of cheshire)
- Adapt template to allow babashka usage
- Add slack badge

## 0.1.15 (19-04-2021)

- Add partial support for GraalVMEE (PGO optimizations in progress)
- Move code to separate packages
- Add interceptors support
- Make call to lambda a private function
- Rename fierycod.holy-lambda.core/gen-main to fierycod.holy-lambda.native-runtime/entrypoint (one targeting java runtime should not depend on native-runtime namespace)
- Provide agent/in-context to ease generation of native-configurations
- Unify responses. Response should be a valid map or nil for event ACK. Text/plain should be send via response/text.
- Add some docs
- Fixup tests

## 0.1.2 (05-04-2021)

- Add full support for async-retriever (look at examples/hello-world & https://github.com/FieryCod/holy-lambda-async-retriever)
- Remove unecessary steps for CircleCI
- Add tests. Allow async handlers (future, promise, map)
- Add CHANGELOG.md
- Provide response utils based on ring-core. Add example of redirect

## 0.1.1 (05-04-2021) (Breaking change) 
Handler definition has been changed from `[event,context] -> response` to `[request] -> response`.

- Fixup reflection
- Add deps.edn; Pregenerate routes; Split runtimes; Add tests
- Switch to rum based macro. Add hook support based on rum hook
- Switch from data.json to jsonista + fixes
- Use Clojure in :scope "provided"
- Handle not existing invocation-id
- (Breaking change) Don't keywordize envs. Tidy up runtime.
- Use only one arity lambdas
- Remove logging system. User should use 3rd party logging implementations
- Update coordinates to docker image
- Allow pass of --static parameter with --libc=musl
- remove 'choly' (#22)
- Revert removal of Dockerfile. Bump graalvm
- Add install-graalvm command
- Remove Node.js dependency
- Switch from joker -> clj-kondo
- Example makefile improvements (#19)
- chore(deps): bump bleach from 3.1.0 to 3.1.1 in /src/python/choly (#15)
- chore(deps): bump handlebars from 4.1.2 to 4.5.3 (#14)
- test(agent): fix tests by correctly sorting payloads (thanks @vemv)
- chore(documentation,release-script): reformat documentation & fix releaser

## 0.0.7 (18-07-2019)

- chore(documentation) Introduce the first draft of documentation
- fix(custom_runtime,choly) Keywordize hashmap & fix CodeUri gen
- chore(test,readme.md): Change badge & attempt to fix test run
- chore(package.json): bump git-cz dependencies

## 0.0.5 (17-07-2019)

- feat(choly,agent): Add basic choly cli tool & agent executor
- Initial repro for https://github.com/oracle/graal/issues/1367
- refactor(core,agent): Move the core to separate namespaces & implement draft of GraalVM agent
- fix(cljdoc): Fix cljdoc build
- fix(circleci): Fix CircleCi configuration

## 0.0.2 (09-06-2019)

- ci(circleci): Add CircleCi automation
- feat(sqs-example): Add reproducible error in sqs-example for GraalVM team
- feat(example, runtime, version): Add sqs example & bump the version of Clojure dependency
- feat(graalvm,dockerfile): Change the default version of GraalVM to the latest stable one
- feat(runtime,makefiles,readme): Add custom logger & log the runtime fatals
- feat(runtime,makefiles): Add workable lambda for both runtimes
- feat(runtime,fnative): Add a very first version of holy-lambda
