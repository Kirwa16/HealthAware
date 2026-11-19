# HealthAware Android App

A Kotlin + Jetpack Compose + Material 3 starter application for the HealthAware diabetes screening and awareness project.

## Included
- Home dashboard
- Screening introduction and consent gate
- 11-feature questionnaire split into three sections
- Input validation and review screen
- Demonstration result screen (no real model inference)
- Learn and Profile sections
- Non-diagnostic and dataset limitation notices

## Important modelling notes
- The 11 model inputs are: `Age`, `BMI`, `HighBP`, `HighChol`, `Stroke`, `HeartDiseaseorAttack`, `PhysActivity`, `GenHlth`, `PhysHlth`, `MentHlth`, `DiffWalk`.
- Age is mapped to the BRFSS age category 1–13.
- `GenHlth` maps Excellent–Poor to 1–5.
- Binary answers map No=0 and Yes=1.
- Poor-health day counts map to 0–30.
- The current app calculates continuous BMI from height and weight for display only. **Do not send this value to the model yet.** Confirm the exact BMI derivation/rounding and feature preprocessing for the final training dataset first.
- The app currently does not call an API or load a model. The result is explicitly marked as a demonstration.

## Open in Android Studio
1. Extract the ZIP.
2. In Android Studio, choose **Open** and select the extracted `HealthAware` folder.
3. Allow Gradle sync to complete.
4. Use JDK 17 and an Android SDK with API 35 installed.
5. Run the `app` configuration on an emulator or Android device.

This project does not include the binary Gradle Wrapper JAR. Android Studio can open the Gradle project and use its configured Gradle support; if your environment requires a wrapper, generate it from the project root with `gradle wrapper --gradle-version 8.9`.

## Before real deployment
- Confirm BMI encoding against the final model training pipeline.
- Confirm the 11-feature model's exact feature order and preprocessing.
- Connect a secured inference API or a validated on-device model.
- Add an approved privacy notice, retention/deletion controls and consent process.
- Test on devices and with users under the required university ethics/supervisor approval.
- Validate model performance and relevance in the intended population. The current source dataset is from a US survey and is not automatically representative of Kenya.
