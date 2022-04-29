#!/usr/bin/env bash

ignore_files=".git|node_modules|_templates|customize|README.md|build|.idea"
ignore_files="$ignore_files|.gradle|*.jar|kotlin-js-store|exec_template.sh|.DS_Store|gradlew.bat"

for input_file in `tree -I "${ignore_files}" -Ffai --noreport`
do
  if [ ! -d "${input_file}" ]; then
    echo "Processing file: ${input_file}"
    [[ -x "${input_file}" ]] && EXEC=1 || EXEC=0
         jinja -d ./project.yaml ${input_file} -o ./out
         mv ./out ${input_file}
    if [[ "$EXEC" -eq "1" ]] ; then
      echo ${input_file} should be executable.
      chmod +x ${input_file}
    fi
  fi
done

# Clean up / implode
#rm README.md
#mv README_TEMPLATE.md README.md
#mv github .github
#rm customize