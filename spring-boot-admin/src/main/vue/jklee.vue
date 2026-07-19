<template>
  <sba-instance-section :error="error" :loading="!hasLoaded">
    <sba-panel :title="$t('jklee.ui.results')">
      <div class="grid gap-4 mt-2" style="grid-template-columns: repeat(auto-fill, minmax(20rem, 1fr));">
        <template v-for="result in results" :key="result.name">
        <sba-button
          as="a"
          :href="`instances/${instance.id}/actuator/jkleeFiles/${result.name}`"
        >
          <span class="flex items-center gap-2 w-full">
            <font-awesome-icon icon="download"/>
            <span v-text="result.name"/>
            <span class="text-sm opacity-60 ml-auto" v-text="formatEndedAt(result.endedAt)"/>
          </span>
        </sba-button>
        </template>
      </div>

    </sba-panel>

    <sba-panel :title="$t('jklee.ui.session.title')">
      <div class="flex gap-4 flex-col lg:flex-row">
        <div class="flex-1">
          <sba-input
            v-model="profileRequest.sessionName"
            :label="$t('jklee.ui.session.name')"
            :placeholder="$t('jklee.ui.session.name')"
          />
        </div>
        <div class="flex-1">
          <sba-select
            name="format"
            :label="$t('jklee.ui.session.format')"
            :options="profileProperties.formats"
            v-model="profileRequest.format"
          />
        </div>
        <div class="flex-1">
          <sba-input
            :label="$t('jklee.ui.session.duration')"
            v-model="profileRequest.duration"
            :placeholder="$t('jklee.ui.session.duration')"
          />
        </div>
      </div>
      <div class="flex gap-4 items-end mt-4">
        <div class="flex-1">
          <sba-input
            v-model="profileRequest.rawArguments"
            :label="$t('jklee.ui.session.arguments')"
            :placeholder="$t('jklee.ui.session.arguments')"/>
        </div>
        <sba-button
          primary
          :disabled="profiling"
          @click="profile(profileRequest)"
        >
          <template v-if="!profiling">
            <font-awesome-icon icon="download"/>
            <span v-text="$t('jklee.ui.session.start')"/>
          </template>
          <template v-else>
            <span class="inline-flex items-center justify-center">
              <svg
                viewBox="0 0 36 36"
                class="w-10 h-10"
                fill="none"
                stroke="currentColor"
              >
                <circle cx="18" cy="18" r="16" stroke-width="2" opacity="0.3"/>
                <line
                  x1="18"
                  y1="18"
                  x2="18"
                  y2="5"
                  stroke-width="2"
                  stroke-linecap="round"
                  :transform="`rotate(${clockHandAngle} 18 18)`"
                />
                <text
                  x="18"
                  y="19"
                  text-anchor="middle"
                  dominant-baseline="middle"
                  fill="currentColor"
                  stroke="none"
                  font-size="8"
                >{{ Math.floor(progressPercentage) }}%</text>
              </svg>
            </span>
          </template>
        </sba-button>
      </div>

    </sba-panel>

    <sba-panel :title="$t('jklee.ui.settings')">
      <sba-key-value-table :map="settingsMap"/>
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
      sessionName: '',
      rawArguments: 'start,event=itimer,interval=1ms',
      duration: '2s',
      format: 'FLAMEGRAPH',
    },
    results: [],
    profiling: false,
    profilingStartTime: null,
    profilingDuration: 0,
    elapsedTime: 0,
    progressTimer: null
  }),
  computed: {
    progressPercentage() {
      if (!this.profiling || this.profilingDuration === 0) {
        return 0;
      }
      return Math.min(100, (this.elapsedTime / this.profilingDuration) * 100);
    },
    clockHandAngle() {
      return this.progressPercentage * 3.6;
    },
    settingsMap() {
      return this.settings.data.reduce((acc, setting) => {
        acc[setting.name] = setting.value;
        return acc;
      }, {});
    },
  },
  methods: {
    formatEndedAt(endedAt) {
      if (!endedAt) return '';
      const date = new Date(endedAt);
      if (isNaN(date.getTime())) return endedAt;
      const pad = (n) => String(n).padStart(2, '0');
      const time = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
      const now = new Date();
      const isToday =
        date.getFullYear() === now.getFullYear() &&
        date.getMonth() === now.getMonth() &&
        date.getDate() === now.getDate();
      if (isToday) return time;
      const day = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
      return `${day} ${time}`;
    },
    formatDuration(iso) {
      if (!iso) return '';
      const match = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?$/.exec(iso);
      if (!match) return iso;
      const hours = parseInt(match[1] || '0', 10);
      const minutes = parseInt(match[2] || '0', 10);
      const seconds = parseFloat(match[3] || '0');
      if (hours && !minutes && !seconds) return `${hours}h`;
      if (!hours && minutes && !seconds) return `${minutes}m`;
      if (!hours && !minutes) {
        return Number.isInteger(seconds) ? `${seconds}s` : `${Math.round(seconds * 1000)}ms`;
      }
      const totalSeconds = hours * 3600 + minutes * 60 + seconds;
      return Number.isInteger(totalSeconds) ? `${totalSeconds}s` : `${Math.round(totalSeconds * 1000)}ms`;
    },
    parseToMillis(timeString) {
      if (!timeString) return 0;

      const value = parseInt(timeString, 10);
      if (isNaN(value)) return 0;

      const unit = timeString.slice(-1);

      switch(unit) {
        case 's': return value * 1000;
        case 'm': return value * 60 * 1000;
        case 'h': return value * 60 * 60 * 1000;
        case 'd': return value * 60 * 60 * 24 * 1000;
        default: return value; // Assume milliseconds if no unit
      }
    },
    async profile(profileRequest) {
      this.profiling = true;
      try {
        const durationInMillis = this.parseToMillis(profileRequest.duration);
        this.profilingDuration = durationInMillis;
        this.profilingStartTime = Date.now();
        this.elapsedTime = 0;

        // Start the progress timer
        this.startProgressTimer();

        // Start the profiling request
        await this.instance.axios.post(
          `actuator/jkleeProfile/${profileRequest.sessionName}`,
          {
            rawArguments: profileRequest.rawArguments,
            duration: profileRequest.duration,
            format: profileRequest.format,
          }
        );

        // Set a timeout to update the UI when profiling is expected to complete
        setTimeout(() => {
          this.stopProgressTimer();
          this.profiling = false;
          this.updateResultsList();
        }, durationInMillis);
      } catch (error) {
        this.stopProgressTimer();
        this.profiling = false;
        this.error = error;
      }
    },
    startProgressTimer() {
      this.progressTimer = setInterval(() => {
        this.elapsedTime = Date.now() - this.profilingStartTime;
      }, 100); // Update every 100ms for smoother progress
    },
    stopProgressTimer() {
      if (this.progressTimer) {
        clearInterval(this.progressTimer);
        this.progressTimer = null;
      }
    },
    async updateResultsList() {
      try {
        const response = await this.instance.axios.get('actuator/jkleeFiles');
        this.results = response.data.results;
        const formFields = response.data.formFields;
        if (formFields) {
          this.profileRequest.sessionName = formFields.sessionName;
          this.profileRequest.rawArguments = formFields.rawArguments;
          this.profileRequest.duration = this.formatDuration(formFields.duration);
          this.profileRequest.format = formFields.format;
        }
      } catch (error) {
        this.error = error;
      }
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
  },
  beforeUnmount() {
    this.stopProgressTimer();
  }
};
</script>
