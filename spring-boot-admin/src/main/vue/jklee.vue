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
    <div
      v-for="file in resultFiles.files"
      :key="file.id"
    >
      <button
        class="button"
        @click="downloadProfilerResult(file)"
      >
        <font-awesome-icon icon="download" />&nbsp;
        <span v-text="file" />
      </button>
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
        @click="profile(profileSessionName, profileRawArguments, profileDuration, profileFormat)"
      >
        <font-awesome-icon icon="download" />&nbsp;
        <span>start profiling</span>
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
    profileRawArguments: 'start,event=cpu,interval=1ms',
    profileSessionName: 'test',
    profileDuration: '2s',
    profileFormat: 'FLAMEGRAPH',
  }),
  methods: {
    async downloadProfilerResult(fileName) {
      this.instance.axios.get(
        `actuator/jklee-files/${fileName}`,
        {headers: {'Accept': 'text/*,application/*'}},
      ).then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName); //or any other extension
        document.body.appendChild(link);
        link.click();
      });
    },
    async profile(sessionName, rawArguments, profileDuration, profileFormat) {
      this.instance.axios.post(
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
  },
  async created() {
    const response = await this.instance.axios.get('actuator/jklee-settings');
    this.settings = response.data;
    const resultFiles = await this.instance.axios.get('actuator/jklee-files');
    this.resultFiles = resultFiles.data;
  }
};
</script>

<style>
.jklee {
  font-size: 12pt;
  width: 80%;
}
</style>
