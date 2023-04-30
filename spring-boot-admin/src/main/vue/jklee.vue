<!--
  - Show raw settings string for current instance
  - Show settings pretty-printed
  - Show list of currently available VM log files as links to download them
  - Form to update settings (in separate view most certainly)
  -

-->
<template>
  <div class="jklee">
    <pre>Raw settings:<br><span v-text="settings" /></pre>
    <div class="file-list">
      <div
        v-for="file in resultFiles.files"
        :key="file"
        class="file"
      >
        <a
          class="btn btn-primary"
          :href="`instances/${instance.id}/actuator/jklee-files/${file}`"
        >
          <i class="fa fa-download"></i>&nbsp;
          <span v-text="file" />
        </a>
      </div>
    </div>

    <div>
      sessionName:
      <input
        type="text"
        v-model="profileSessionName"
      >
      <br>
      rawArguments:
      <input
        type="text"
        v-model="profileRawArguments"
      >
      <br>
      profileFormat:
      <input
        type="text"
        v-model="profileFormat"
      >
      <br>
      profileDuration:
      <input
        type="text"
        v-model="profileDuration"
      >
      <br>
      <button
        class="button"
        :disabled="submitting"
        @click="profile(profileSessionName, profileRawArguments, profileDuration, profileFormat)"
      >
        <template v-if="!submitting">
          <font-awesome-icon icon="download" />&nbsp;
          <span>start profiling</span>
        </template>
        <template v-else>
          <div class="loader">Profiling...</div>
        </template>
      </button>
    </div>
  </div>
</template>
<script>

export default {
  props: {
    instance: {
      type: Object,
      required: true
    }
  },
  data: () => ({
    settings: '',
    resultFiles: '',
    profileRawArguments: 'start,event=itimer,interval=1ms',
    profileSessionName: 'test',
    profileDuration: '2s',
    profileFormat: 'FLAMEGRAPH',
    submitting: false,
  }),
  methods: {
    async profile(sessionName, rawArguments, profileDuration, profileFormat) {
      this.submitting = true;
      const durationInMillis = parseToMillis(profileDuration);
      setTimeout(() => {
        this.submitting = false;
        this.updateResultsList()
      }, durationInMillis)

      await this.instance.axios.post(
        `actuator/jklee-profile/${sessionName}`,
        {
          rawArguments: rawArguments,
          duration: profileDuration,
          format: profileFormat,
        }
      )
        .then((response) => {
          console.log(response)
        })
    },
    async updateResultsList() {
      const resultFiles = await this.instance.axios.get('actuator/jklee-files');
      this.resultFiles = resultFiles.data;

    }
  },
  async created() {
    const response = await this.instance.axios.get('actuator/jklee-settings');
    this.settings = response.data;
    await this.updateResultsList()
  }
};

function parseToMillis(timeString) {
  let value = parseInt(timeString, 10);
  let unit = timeString.slice(-1);
  let millis = 0;

  if (unit === 's') {
    millis = value * 1000;
  } else if (unit === 'm') {
    millis = value * 60 * 1000;
  } else if (unit === 'h') {
    millis = value * 60 * 60 * 1000;
  } else if (unit === 'd') {
    millis = value * 60 * 60 * 24 * 1000;
  }

  return millis;
}
</script>

<style>

.loader {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  width: 0;
  background-color: #ffffff;
  animation: fill 2s ease-out forwards;
}

@keyframes fill {
  from { width: 0; }
  to { width: 100%; }
}

.jklee {
  font-size: 12pt;
  width: 80%;
}

pre {
  margin: 0;
  font-size: 10pt;
}

button {
  background-color: #4caf50;
  border: none;
  color: white;
  padding: 8px 16px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12pt;
  margin: 4px 2px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 3px;
  width: 100%;
}

input[type="text"] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 12pt;
}

a.button {
  background-color: #4caf50;
  border: none;
  color: white;
  padding: 8px 16px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 12pt;
  margin: 4px 2px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 3px;
}

a.button:hover {
  background-color: #3e8e41;
}

span {
  font-size: 12pt;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.file {
  margin-right: 10px;
  margin-bottom: 10px;
}

.font-awesome-icon {
  font-size: 12pt;
  margin-right: 4px;
}

/* CSS for loader animation */
.loader:before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  width: 0;
  background-color: #ffffff;
  animation: fill 2s ease-out forwards;
}

@keyframes fill {
  from { width: 0; }
  to { width: 100%; }
}

</style>
