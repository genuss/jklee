<template>
  <sba-instance-section :error="error" :loading="!hasLoaded">
    <sba-panel :title="$t('jklee.ui.results')">
      <div class="grid grid-cols-2 gap-4">
        <template v-for="result in results">
          <div class="border p-2 flex">
            <div class="w-1/2 pr-2">
              <a :href="`instances/${instance.id}/actuator/jkleeFiles/${result.name}`">
                <font-awesome-icon icon="download" class="mr-2"/>
                <span v-text="result.name"/>
              </a>
            </div>
            <div class="w-1/2" v-text="result.endedAt"/>
          </div>
        </template>
      </div>

    </sba-panel>

    <sba-panel :title="$t('jklee.ui.session.title')">
      <div class="flex gap-6 flex-col lg:flex-row">
        <div class="flex-1">
          <sba-input
            v-model="profileRequest.sessionName"
            :label="$t('jklee.ui.session.name')"
            :placeholder="$t('jklee.ui.session.name')"
          />
        </div>
        <div class="flex-1">
          <sba-select
            :name="$t('jklee.ui.session.format')"
            :label="$t('jklee.ui.session.format')"
            :options="profileProperties.formats"
            v-model="profileRequest.format"
            class="focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md"
          >
          </sba-select>
        </div>
        <div class="flex-1">
          <sba-input
            :label="$t('jklee.ui.session.duration')"
            v-model="profileRequest.duration"
            :placeholder="$t('jklee.ui.session.duration')"
          />
        </div>
      </div>
      <div class="flex">
        <div class="flex-1 mr-4">
          <sba-input
            v-model="profileRequest.rawArguments"
            :label="$t('jklee.ui.session.arguments')"
            :placeholder="$t('jklee.ui.session.arguments')"/>
        </div>
        <sba-button
          :disabled="profiling"
          @click="profile(profileRequest)"
        >
          <template v-if="!profiling">
            <font-awesome-icon icon="download"/>
            <span v-text="$t('jklee.ui.session.start')"/>
          </template>
          <template v-else>
            <span v-text="$t('jklee.ui.session.profiling')"/>
          </template>
        </sba-button>
      </div>

    </sba-panel>

    <sba-panel :title="$t('jklee.ui.settings')">
      <template v-for="(setting, index) in settings.data" :key="setting.name">
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
    settings: {
      data: [],
    },
    profileProperties: {},
    profileRequest: {
      sessionName: 'test',
      rawArguments: 'start,event=itimer,interval=1ms',
      duration: '2s',
      format: 'FLAMEGRAPH',
    },
    results: [],
    profiling: false,
  }),
  methods: {
    async profile(profileRequest) {
      this.profiling = true;
      const durationInMillis = parseToMillis(profileRequest.duration);
      setTimeout(() => {
        this.profiling = false;
        this.updateResultsList()
      }, durationInMillis)

      await this.instance.axios.post(
        `actuator/jkleeProfile/${profileRequest.sessionName}`,
        {
          rawArguments: profileRequest.rawArguments,
          duration: profileRequest.duration,
          format: profileRequest.format,
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
      const settingsResponse = await this.instance.axios.get('actuator/jkleeSettings');
      this.settings.data = settingsResponse.data.settings;

      const profileResponse = await this.instance.axios.get('actuator/jkleeProfile');
      this.profileProperties = profileResponse.data;

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
