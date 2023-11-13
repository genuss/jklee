<!--
  - Show raw settings string for current instance
  - Show settings pretty-printed
  - Show list of currently available VM log files as links to download them
  - Form to update settings (in separate view most certainly)
  -

-->
<template>
  <sba-instance-section :error="error" :loading="!hasLoaded">
    <sba-panel :title="'Settings'">
      <template v-for="(setting, index) in settings" :key="settings.name">
        <div>
          <div class="flex items-center px-4 py-3" :class="{
            'bg-gray-50': index % 2 === 0,
          }">
            <div class="flex-1 sm:break-all">
              <span v-text="setting.name"/>
            </div>
            <div>
              <span v-text="setting.value"/>
            </div>
          </div>
        </div>
      </template>
    </sba-panel>


    <sba-panel :title="'Profiling results'">
      <div class="content info">
        <table class="table">
          <tr
            v-for="result in results"
            :key="result.name"
          >
            <td class="info__key">
              <a
                class="btn btn-primary"
                :href="`instances/${instance.id}/actuator/jkleeFiles/${result.name}`"
              >
                <i class="fa fa-download"></i>&nbsp;
                <span v-text="result.name"/>
              </a>
            </td>
            <td class="info__key" v-text="result.endedAt"/>
          </tr>
        </table>
      </div>
    </sba-panel>

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

      <sba-button
        :disabled="profiling"
        @click="profile(profileSessionName, profileRawArguments, profileDuration, profileFormat)"
      >
        <template v-if="!profiling">
          <font-awesome-icon icon="download"/>&nbsp;
          <span>Start profiling</span>
        </template>
        <template v-else>
          <div class="loader">Profiling...</div>
        </template>
      </sba-button>
    </div>
  </sba-instance-section>

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
    error: null,
    hasLoaded: false,
    settings: [],
    results: [],
    profileRawArguments: 'start,event=itimer,interval=1ms',
    profileSessionName: 'test',
    profileDuration: '2s',
    profileFormat: 'FLAMEGRAPH',
    profiling: false,
  }),
  methods: {
    async profile(sessionName, rawArguments, profileDuration, profileFormat) {
      this.profiling = true;
      const durationInMillis = parseToMillis(profileDuration);
      setTimeout(() => {
        this.profiling = false;
        this.updateResultsList()
      }, durationInMillis)

      await this.instance.axios.post(
        `actuator/jkleeProfile/${sessionName}`,
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
      const response = await this.instance.axios.get('actuator/jkleeFiles');
      this.results = response.data.results;
    }
  },
  async created() {
    try {
      const response = await this.instance.axios.get('actuator/jkleeSettings');
      this.settings = response.data.settings;
    } catch (error) {
      this.error = error
    } finally {
      this.hasLoaded = true;
    }
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
