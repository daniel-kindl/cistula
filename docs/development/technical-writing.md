# Technical writing

Follow ASD-STE100 principles where they help. We reference the standard. We do not copy it.

- One topic per sentence. Short sentences (instructions: about 20 words or fewer).
- Active voice and imperative mood for instructions: "Run `./eng/verify.sh`."
- Use one word for one meaning. Do not mix "check", "gate", and "job" for the same thing.
- Put the most important information first.
- Use tables for versions, commands, and mappings.
- Do not apply these rules to Kotlin identifiers, API names, or quoted tool output.
- No marketing language. State facts and reasons.

## Examples

These examples apply the rules above. They are not text from ASD-STE100. This repository does not copy the ASD-STE100 dictionary.

The Before column quotes weak sentences. Do not copy those sentences.

| Before | After |
| --- | --- |
| `It is worth noting that verification should be performed in order to make sure that no warnings remain.` | Run `./eng/verify.sh`. The script fails when a warning remains. |
| `The file is deleted by the agent if the file is not needed.` | The agent deletes the file when the file is not needed. |
| `Perform an analysis of the log and then check whether the log matches the expected text.` | Analyze the log. Compare the log with the expected text. |
