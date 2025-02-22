# Migration Guide
## 0.6.0 -> 0.6.2
### Tasks
  1. Add a `holy-lambda` dependency in `bb.edn`
  
    **bb.edn**
    ```clojure
    {:deps {...
            io.github.FieryCod/holy-lambda {:mvn/version "0.6.2"}}
    ```
  
  2. Update the `:sha` version of `io.github.FieryCod/holy-lambda-babashka-tasks` to `e6c47274a2bfc7576a9da0ccdbc079c1e83bee17`.
  
    **bb.edn**
    ```clojure
    {:deps {io.github.FieryCod/holy-lambda-babashka-tasks
            {:git/url   "https://github.com/FieryCod/holy-lambda"
             :deps/root "./modules/holy-lambda-babashka-tasks"
             :sha       "e6c47274a2bfc7576a9da0ccdbc079c1e83bee17"}}}
    ```
  
  3. Add `hl:update-bb-tasks` in `bb.edn`.
  
    **bb.edn**
    ```clojure
    :tasks {:requires            ([holy-lambda.tasks])
            hl:docker:run        holy-lambda.tasks/hl:docker:run
            hl:native:conf       holy-lambda.tasks/hl:native:conf
            hl:native:executable holy-lambda.tasks/hl:native:executable
            hl:babashka:sync     holy-lambda.tasks/hl:babashka:sync
            hl:compile           holy-lambda.tasks/hl:compile
            hl:doctor            holy-lambda.tasks/hl:doctor
            hl:clean             holy-lambda.tasks/hl:clean
            hl:update-bb-tasks   holy-lambda.tasks/hl:update-bb-tasks
            hl:version           holy-lambda.tasks/hl:version}}
    ```

### Babashka backend
  New version of the backend (0.6.2) doesn't ship the `holy-lambda` artifact, therefore you have to pack the `holy-lambda` yourself.
  
  **Migrating to new backend version:**
  
  1. Deploy new version of the backend:
    - [AMD64](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-amd64)
    - [ARM64](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-arm64)
    
  2. Reference a new layer version ARN in `template.yml`
  3. Add a local layer that points to `.holy-lambda/bb-clj-deps`.
  4. Run `bb hl:babashka:sync` to populate `.holy-lambda/bb-clj-deps`.
    
    **Example:**
    ```yml
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
          - arn:aws:lambda:eu-central-1:443526418261:layer:holy-lambda-babashka-runtime-amd64:3
          - !Ref BabashkaDepsLayer 
    ```
  5. Deploy!

## 0.5.0 -> 0.6.0
### Tasks
  The new version of HL removes the `hl:sync` step, therefore the development should be faster now.
  To take the advantage of the new tasks release follow these steps.

  1. Remove the following properties from `bb.edn`
    - `:mvn/local-repo`
    - `:build/clj-alias`
    - `:runtime/entrypoint`
  2. Replace `:tasks` with the following set of tasks:
  
    ```clojure
    :tasks {:requires            ([holy-lambda.tasks])
            hl:docker:run        holy-lambda.tasks/hl:docker:run
            hl:native:conf       holy-lambda.tasks/hl:native:conf
            hl:native:executable holy-lambda.tasks/hl:native:executable
            hl:babashka:sync     holy-lambda.tasks/hl:babashka:sync
            hl:compile           holy-lambda.tasks/hl:compile
            hl:doctor            holy-lambda.tasks/hl:doctor
            hl:clean             holy-lambda.tasks/hl:clean
            hl:version           holy-lambda.tasks/hl:version}}
     ``` 
     
   3. Replace the `:sha` in `bb.edn` with the newest tag version: `eb299bf6e380bcc8e484e80f8f16363bc5deb41c`
   4. Remove `:mvn/local-repo` property from `deps.edn`
   5. Replace `:uberjar` alias in `deps.edn` with the following:
    ```clojure
    {:uberjar {:replace-deps {com.github.seancorfield/depstar {:mvn/version "2.1.303"}}
                :exec-fn      hf.depstar/uberjar
                :exec-args    {:aot        ["<VALUE_OF_ENTRYPOINT>"]
                              :main-class "<VALUE_OF_ENTRYPOINT>"
                              :jar        ".holy-lambda/build/output.jar"
                              :jvm-opts   ["-Dclojure.compiler.direct-linking=true"
                                            "-Dclojure.spec.skip-macros=true"]}}}
    ```
   6. Add `:build/compile-cmd` property with `clojure -X:uberjar` command in string.
   7. Rename `:runtime` to `:backend` property in bb.edn.

### Backends
#### Babashka
Babashka backend now supports `arm64` and `amd64` architectures on `Amazon Linux 2`. 
> :information_source: **The old [layer](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime) holy-lambda-babashka-runtime is now deprecated**

instead one of the following layers should be used:
  - [amd64](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-amd64) (drop in replacement for now deprecated layer)
  - [arm64](https://serverlessrepo.aws.amazon.com/applications/eu-central-1/443526418261/holy-lambda-babashka-runtime-arm64)

The difference between both versions is in cold starts, where `arm64` has at least 100ms cold start improvement.
Additionally for downloading pods & clojure deps the new `bb hl:babashka:sync` is used. 
  
  > :information_source: **ARM64 version requires a arm64/aarch64 version of holy-lambda-builder**
  

Babashka runtime requires additional environment variable (previously `Entrypoint`) now `HL_ENTRYPOINT`.
  
#### Docker images 
DockerHub version of `fierycod/graalvm-native-image` builder has been deprecated. 
Use new `holy-lambda-builder`. 

See [here](https://github.com/FieryCod/holy-lambda/pkgs/container/holy-lambda-builder)


