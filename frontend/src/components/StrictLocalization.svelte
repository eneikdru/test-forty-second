<script>
  // Language toggle: default is English, true means strict Russian localization applied
  export let strictRussian = true;

  // Form states
  let showForm = false;
  let patientName = "";
  let locationName = "";
  let specialty = "Epidemiology";
  let severity = "Medium";

  // Form errors
  let errors = {};
  let formSubmittedSuccessfully = false;

  // Dictionary of translations
  const t = {
    en: {
      appName: "EpidAccounting",
      adminTitle: "Administrator",
      subTitle: "CRIE Epidemiology Center",
      home: "Dashboard",
      patientRegistry: "Patient Registry",
      epidOutbreaks: "Epid-outbreaks",
      laboratory: "Laboratory",
      archive: "Archive",
      help: "Help",
      controlPanel: "Control Panel",
      operationalSummary: "Summary of the operational epidemiological situation",
      totalCases: "Total Cases",
      weeklyTrend: "+3.2% weekly",
      activeOutbreaks: "Active Outbreaks",
      highPriorityCount: "High priority: 12",
      pendingReports: "Pending Reports",
      awaitingVerification: "Awaiting verification",
      recentAlerts: "Recent Alerts",
      allAlerts: "All Alerts",
      alertMeasles: "New Infection Outbreak (Measles)",
      alertMeaslesDesc: "3 new cases recorded in preschool No. 45. Urgent routing required.",
      alertLab: "Laboratory Data Update",
      alertLabDesc: "PCR test results received (Batch 1024-A). Available for analysis.",
      alertCheck: "Planned Rospotrebnadzor Inspection",
      alertCheckDesc: "A summary report for Q1 must be prepared by 15:00 tomorrow.",
      quickActions: "Quick Actions",
      registerCase: "Register Case",
      uploadRegistry: "Upload Registry",
      notifications: "Notifications",
      langToggleLabel: "Language Toggle",
      langEnglish: "English Default",
      langRussian: "Strict Russian",

      // Form translation
      formTitle: "Register Outbreak Case",
      patientNameLabel: "Patient Full Name",
      patientNamePlaceholder: "e.g. John Doe",
      locationLabel: "Outbreak Location",
      locationPlaceholder: "e.g. Ward 4",
      specialtyLabel: "Specialty",
      severityLabel: "Severity Level",
      submitButton: "Submit Case",
      cancelButton: "Cancel",
      successMsg: "Case successfully registered!",

      // Specialties
      specEpidemiology: "Epidemiology",
      specInfectious: "Infectious Diseases",
      specPediatrics: "Pediatrics",

      // Severity
      sevLow: "Low",
      sevMedium: "Medium",
      sevHigh: "High",

      // Validation errors
      errNameRequired: "Patient Name is required",
      errNameTooShort: "Patient Name must be at least 3 characters",
      errLocationRequired: "Location is required",
    },
    ru: {
      appName: "ЭпидУчет",
      adminTitle: "Администратор",
      subTitle: "ФГБУ Эпидемиологии",
      home: "Главная",
      patientRegistry: "Реестр пациентов",
      epidOutbreaks: "Эпид-очаги",
      laboratory: "Лаборатория",
      archive: "Архив",
      help: "Помощь",
      controlPanel: "Панель управления",
      operationalSummary: "Сводка оперативной эпидемиологической обстановки",
      totalCases: "Всего случаев",
      weeklyTrend: "+3.2% за неделю",
      activeOutbreaks: "Активные очаги",
      highPriorityCount: "Высокий приоритет: 12",
      pendingReports: "Отчеты в очереди",
      awaitingVerification: "Ожидают проверки",
      recentAlerts: "Последние уведомления",
      allAlerts: "Все уведомления",
      alertMeasles: "Новый очаг инфекции (Корь)",
      alertMeaslesDesc: "Зафиксировано 3 новых случая в ДОУ №45. Требуется срочная маршрутизация.",
      alertLab: "Обновление лабораторных данных",
      alertLabDesc: "Поступили результаты ПЦР-тестов (Партия 1024-А). Доступны для анализа.",
      alertCheck: "Плановая проверка Роспотребнадзора",
      alertCheckDesc: "Необходимо подготовить сводный отчет за I квартал до 15:00 завтрашнего дня.",
      quickActions: "Быстрые действия",
      registerCase: "Зарегистрировать случай",
      uploadRegistry: "Загрузить реестр",
      notifications: "Уведомления",
      langToggleLabel: "Переключатель языка",
      langEnglish: "Английский (по умолчанию)",
      langRussian: "Строгий русский (локализация)",

      // Form translation
      formTitle: "Регистрация случая заболевания",
      patientNameLabel: "ФИО пациента",
      patientNamePlaceholder: "например, Иван Иванов",
      locationLabel: "Очаг заболевания",
      locationPlaceholder: "например, Палата 4",
      specialtyLabel: "Специальность",
      severityLabel: "Уровень опасности",
      submitButton: "Зарегистрировать случай",
      cancelButton: "Отмена",
      successMsg: "Случай успешно зарегистрирован в базе данных!",

      // Specialties
      specEpidemiology: "Эпидемиология",
      specInfectious: "Инфекционные болезни",
      specPediatrics: "Педиатрия",

      // Severity
      sevLow: "Низкий",
      sevMedium: "Средний",
      sevHigh: "Высокий",

      // Validation errors
      errNameRequired: "ФИО пациента обязательно для заполнения",
      errNameTooShort: "ФИО пациента должно содержать не менее трех символов",
      errLocationRequired: "Очаг заболевания обязателен для заполнения",
    }
  };

  $: cur = strictRussian ? t.ru : t.en;

  function validateForm() {
    errors = {};
    if (!patientName.trim()) {
      errors.patientName = cur.errNameRequired;
    } else if (patientName.trim().length < 3) {
      errors.patientName = cur.errNameTooShort;
    }

    if (!locationName.trim()) {
      errors.locationName = cur.errLocationRequired;
    }

    return Object.keys(errors).length === 0;
  }

  function handleSubmit() {
    formSubmittedSuccessfully = false;
    if (validateForm()) {
      formSubmittedSuccessfully = true;
      setTimeout(() => {
        patientName = "";
        locationName = "";
        showForm = false;
        formSubmittedSuccessfully = false;
        errors = {};
      }, 2000);
    }
  }

  function handleCancel() {
    showForm = false;
    errors = {};
    patientName = "";
    locationName = "";
    formSubmittedSuccessfully = false;
  }
</script>

<div class="bg-background text-on-background font-body-md min-h-screen flex flex-col md:flex-row relative">
  <!-- NavigationDrawer (Web) -->
  <aside class="hidden md:flex flex-col p-4 gap-stack-sm bg-surface dark:bg-surface-container-low h-full w-80 rounded-r-xl shadow-sm dark:shadow-none fixed top-0 left-0 z-40">
    <div class="flex items-center gap-4 mb-stack-lg p-2">
      <div class="h-12 w-12 rounded-full overflow-hidden bg-surface-variant flex items-center justify-center">
        <img class="object-cover w-full h-full" alt="Professional health administrator" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCRk_YhZ7gDypWvOLco7lc5QMmqyLgTP9g7OPLH7bS7faXRS_CYbXV1v6av7GoJRbHLONAbKjZH-ysAiHrkyggXJ29T7L4KMd_fbjLaOolCaikEQj8UIrQur0nbVNZ-R5c4bgmb47PlHRiT0_xXhEcSfbjmC-4Ghg9PDnIev1YAbMkYNq2FDBTKg2taUXhytSUsYjSck-y2vqWhRKoUv0Dkauh9_tpm9gYRvXelqfA5oBQLgqSjD6-MnMs9GC6NDQOFsc5lLTU3YVKY"/>
      </div>
      <div class="flex flex-col text-left">
        <span class="font-headline-sm text-headline-sm text-primary">{cur.adminTitle}</span>
        <span class="font-body-sm text-body-sm text-on-surface-variant">{cur.subTitle}</span>
      </div>
    </div>

    <nav class="flex flex-col gap-2 flex-grow">
      <a class="flex items-center gap-3 px-4 py-3 bg-secondary-container text-on-secondary-container font-bold rounded-full transition-all duration-150" href="#dashboard">
        <span class="material-symbols-outlined">dashboard</span>
        <span class="font-body-md text-body-md">{cur.home}</span>
      </a>
      <a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-all duration-150" href="#patients">
        <span class="material-symbols-outlined">person_search</span>
        <span class="font-body-md text-body-md">{cur.patientRegistry}</span>
      </a>
      <a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-all duration-150" href="#outbreaks">
        <span class="material-symbols-outlined">location_on</span>
        <span class="font-body-md text-body-md">{cur.epidOutbreaks}</span>
      </a>
      <a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-all duration-150" href="#laboratory">
        <span class="material-symbols-outlined">biotech</span>
        <span class="font-body-md text-body-md">{cur.laboratory}</span>
      </a>
      <a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-all duration-150" href="#archive">
        <span class="material-symbols-outlined">inventory_2</span>
        <span class="font-body-md text-body-md">{cur.archive}</span>
      </a>
    </nav>

    <div class="mt-auto">
      <a class="flex items-center gap-3 px-4 py-3 text-on-surface-variant hover:bg-surface-container-high rounded-full transition-all duration-150" href="#help">
        <span class="material-symbols-outlined">help</span>
        <span class="font-body-md text-body-md">{cur.help}</span>
      </a>
    </div>
  </aside>

  <!-- Main Content Wrapper -->
  <div class="flex-1 flex flex-col md:ml-80 pb-20 md:pb-0 min-h-screen">
    <!-- TopAppBar (Web) -->
    <header class="hidden md:flex justify-between items-center px-container-padding w-full h-14 bg-surface dark:bg-surface-container-low border-b border-outline-variant dark:border-outline sticky top-0 z-30 transition-colors duration-200">
      <div class="flex items-center gap-4">
        <span class="material-symbols-outlined text-primary dark:text-primary-fixed">medical_services</span>
        <h1 class="font-headline-md text-headline-md font-bold text-primary dark:text-primary-fixed">{cur.appName}</h1>
      </div>

      <!-- Language Toggle Control (Accessible & Styled) -->
      <div class="flex items-center gap-2 bg-surface-container rounded-full px-3 py-1 border border-outline-variant">
        <span class="material-symbols-outlined text-sm text-secondary">language</span>
        <button
          id="toggle-en"
          class="text-xs px-2 py-0.5 rounded-full transition-colors {!strictRussian ? 'bg-primary text-on-primary font-semibold' : 'text-on-surface-variant hover:bg-surface-variant'}"
          on:click={() => { strictRussian = false; errors = {}; }}
        >
          EN
        </button>
        <button
          id="toggle-ru"
          class="text-xs px-2 py-0.5 rounded-full transition-colors {strictRussian ? 'bg-primary text-on-primary font-semibold' : 'text-on-surface-variant hover:bg-surface-variant'}"
          on:click={() => { strictRussian = true; errors = {}; }}
        >
          RU
        </button>
      </div>

      <div class="flex items-center gap-4">
        <button class="p-2 rounded-full text-on-surface-variant dark:text-on-surface-variant hover:bg-surface-container dark:hover:bg-surface-container-high transition-colors duration-200" aria-label={cur.notifications}>
          <span class="material-symbols-outlined">notifications</span>
        </button>
      </div>
    </header>

    <!-- TopAppBar (Mobile) -->
    <header class="md:hidden flex justify-between items-center px-container-padding w-full h-14 bg-surface dark:bg-surface-container-low sticky top-0 z-30 border-b border-outline-variant dark:border-outline">
      <div class="flex items-center gap-3">
        <span class="material-symbols-outlined text-primary">medical_services</span>
        <span class="font-headline-md text-headline-md font-bold text-primary">{cur.appName}</span>
      </div>

      <!-- Mobile Language Toggle -->
      <div class="flex items-center gap-1 bg-surface-container rounded-full p-0.5 border border-outline-variant">
        <button
          id="toggle-en-mobile"
          class="text-[10px] px-1.5 py-0.5 rounded-full {!strictRussian ? 'bg-primary text-on-primary' : 'text-on-surface-variant'}"
          on:click={() => { strictRussian = false; errors = {}; }}
        >
          EN
        </button>
        <button
          id="toggle-ru-mobile"
          class="text-[10px] px-1.5 py-0.5 rounded-full {strictRussian ? 'bg-primary text-on-primary' : 'text-on-surface-variant'}"
          on:click={() => { strictRussian = true; errors = {}; }}
        >
          RU
        </button>
      </div>

      <button class="p-2 rounded-full text-on-surface-variant hover:bg-surface-container transition-colors duration-200" aria-label={cur.notifications}>
        <span class="material-symbols-outlined">notifications</span>
      </button>
    </header>

    <!-- Main Canvas -->
    <main class="flex-1 p-container-padding flex flex-col gap-stack-lg max-w-7xl mx-auto w-full">
      <div class="flex flex-col gap-2">
        <h2 class="font-display-lg text-display-lg text-on-surface">{cur.controlPanel}</h2>
        <p class="font-body-lg text-body-lg text-on-surface-variant">{cur.operationalSummary}</p>
      </div>

      <!-- Bento Grid Layout for Metrics & Alerts -->
      <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter">

        <!-- KPI Card 1: Total Cases -->
        <div class="col-span-1 md:col-span-4 bg-surface-container-lowest border border-outline-variant rounded-xl p-6 flex flex-col gap-4 shadow-sm hover:border-primary transition-colors duration-200 cursor-default group">
          <div class="flex justify-between items-start">
            <div class="flex flex-col gap-1">
              <span class="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider">{cur.totalCases}</span>
              <span class="font-display-lg text-display-lg text-primary">12,482</span>
            </div>
            <div class="p-3 bg-surface-container rounded-full text-primary group-hover:bg-primary group-hover:text-on-primary transition-colors duration-200">
              <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">coronavirus</span>
            </div>
          </div>
          <div class="flex items-center gap-2 mt-auto">
            <span class="material-symbols-outlined text-error text-sm">trending_up</span>
            <span class="font-body-sm text-body-sm text-error font-semibold">{cur.weeklyTrend}</span>
          </div>
        </div>

        <!-- KPI Card 2: Active Outbreaks -->
        <div class="col-span-1 md:col-span-4 bg-surface-container-lowest border border-outline-variant rounded-xl p-6 flex flex-col gap-4 shadow-sm hover:border-primary transition-colors duration-200 cursor-default group">
          <div class="flex justify-between items-start">
            <div class="flex flex-col gap-1">
              <span class="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider">{cur.activeOutbreaks}</span>
              <span class="font-display-lg text-display-lg text-error">47</span>
            </div>
            <div class="p-3 bg-error-container rounded-full text-on-error-container group-hover:bg-error group-hover:text-on-error transition-colors duration-200">
              <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">warning</span>
            </div>
          </div>
          <div class="flex items-center gap-2 mt-auto">
            <span class="material-symbols-outlined text-secondary text-sm">location_on</span>
            <span class="font-body-sm text-body-sm text-secondary">{cur.highPriorityCount}</span>
          </div>
        </div>

        <!-- KPI Card 3: Pending Reports -->
        <div class="col-span-1 md:col-span-4 bg-surface-container-lowest border border-outline-variant rounded-xl p-6 flex flex-col gap-4 shadow-sm hover:border-primary transition-colors duration-200 cursor-default group">
          <div class="flex justify-between items-start">
            <div class="flex flex-col gap-1">
              <span class="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider">{cur.pendingReports}</span>
              <span class="font-display-lg text-display-lg text-secondary">156</span>
            </div>
            <div class="p-3 bg-surface-container-high rounded-full text-secondary group-hover:bg-secondary group-hover:text-on-secondary transition-colors duration-200">
              <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">assignment_late</span>
            </div>
          </div>
          <div class="flex items-center gap-2 mt-auto">
            <div class="w-full bg-surface-variant h-1.5 rounded-full overflow-hidden">
              <div class="bg-primary h-full w-2/3"></div>
            </div>
            <span class="font-body-sm text-body-sm text-on-surface-variant whitespace-nowrap">{cur.awaitingVerification}</span>
          </div>
        </div>

        <!-- Recent Alerts List -->
        <div class="col-span-1 md:col-span-8 bg-surface-container-lowest border border-outline-variant rounded-xl overflow-hidden flex flex-col shadow-sm">
          <div class="bg-surface-container-low px-6 py-4 border-b border-outline-variant flex justify-between items-center">
            <h3 class="font-headline-sm text-headline-sm text-on-surface font-semibold">{cur.recentAlerts}</h3>
            <button class="font-label-bold text-label-bold text-primary hover:text-primary-fixed-dim transition-colors uppercase">{cur.allAlerts}</button>
          </div>
          <div class="flex flex-col">
            <!-- Alert Item 1 -->
            <div class="flex items-start gap-4 p-4 border-b border-surface-variant hover:bg-surface-bright transition-colors cursor-pointer">
              <div class="p-2 bg-error-container rounded-full text-on-error-container shrink-0 mt-1">
                <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">emergency</span>
              </div>
              <div class="flex flex-col gap-1 flex-grow">
                <div class="flex justify-between items-start">
                  <span class="font-body-md text-body-md font-semibold text-on-surface">{cur.alertMeasles}</span>
                  <span class="font-body-sm text-body-sm text-on-surface-variant">10м</span>
                </div>
                <span class="font-body-sm text-body-sm text-on-surface-variant">{cur.alertMeaslesDesc}</span>
              </div>
            </div>

            <!-- Alert Item 2 -->
            <div class="flex items-start gap-4 p-4 border-b border-surface-variant hover:bg-surface-bright transition-colors cursor-pointer">
              <div class="p-2 bg-surface-container-high rounded-full text-secondary shrink-0 mt-1">
                <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">science</span>
              </div>
              <div class="flex flex-col gap-1 flex-grow">
                <div class="flex justify-between items-start">
                  <span class="font-body-md text-body-md font-semibold text-on-surface">{cur.alertLab}</span>
                  <span class="font-body-sm text-body-sm text-on-surface-variant">1ч</span>
                </div>
                <span class="font-body-sm text-body-sm text-on-surface-variant">{cur.alertLabDesc}</span>
              </div>
            </div>

            <!-- Alert Item 3 -->
            <div class="flex items-start gap-4 p-4 hover:bg-surface-bright transition-colors cursor-pointer">
              <div class="p-2 bg-primary-container rounded-full text-on-primary-container shrink-0 mt-1">
                <span class="material-symbols-outlined text-lg" style="font-variation-settings: 'FILL' 1;">campaign</span>
              </div>
              <div class="flex flex-col gap-1 flex-grow">
                <div class="flex justify-between items-start">
                  <span class="font-body-md text-body-md font-semibold text-on-surface">{cur.alertCheck}</span>
                  <span class="font-body-sm text-body-sm text-on-surface-variant">Вчера</span>
                </div>
                <span class="font-body-sm text-body-sm text-on-surface-variant">{cur.alertCheckDesc}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Secondary Info Area / Quick Actions -->
        <div class="col-span-1 md:col-span-4 bg-surface-container-lowest border border-outline-variant rounded-xl p-6 flex flex-col gap-4 shadow-sm relative overflow-hidden group">
          <div class="absolute inset-0 opacity-10 pointer-events-none group-hover:opacity-20 transition-opacity duration-500">
            <div class="bg-cover bg-center w-full h-full" style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuBuR7P1p7ZB9x3V6HVslb8hInxAvNOe22yKCUEnyDRs2Ytb_Okkph1IQUw3gbPECvP5agDG8rV6qT5VfdNVEfAK5KhB0FJ86NcIB0EHdf0JELkrz4uxqDVKk1PJxcRf6ERnWh5nYAqxf9tmAlbEPvM6nE4_4uFmIsRlFhJ1W-r0j9W0WMY_X1MaO2JZrUXLSw6CYKpkyYe2D7tiMlIdWnhyFiQJt3EPn0hBy4swEyc1f6HEwN35bAyFQcIO_gNn-caiiJy6uIs66H92')"></div>
          </div>
          <h3 class="font-headline-sm text-headline-sm text-on-surface font-semibold relative z-10">{cur.quickActions}</h3>

          <div class="flex flex-col gap-3 mt-2 relative z-10">
            <!-- Register Case triggers the modal form -->
            <button
              id="register-case-btn"
              class="w-full h-11 bg-primary text-on-primary font-label-bold text-label-bold rounded-lg hover:bg-on-primary-fixed-variant transition-all flex items-center justify-center gap-2 active:scale-95"
              on:click={() => { showForm = true; errors = {}; }}
            >
              <span class="material-symbols-outlined text-sm">add_circle</span>
              {cur.registerCase}
            </button>
            <button class="w-full h-11 bg-transparent border border-outline text-secondary font-label-bold text-label-bold rounded-lg hover:bg-surface-container-low transition-all flex items-center justify-center gap-2 active:scale-95">
              <span class="material-symbols-outlined text-sm">upload_file</span>
              {cur.uploadRegistry}
            </button>
          </div>
        </div>

      </div>
    </main>
  </div>

  <!-- BottomNavBar (Mobile Only) -->
  <nav class="md:hidden fixed bottom-0 w-full flex justify-around items-center h-16 pb-safe bg-surface dark:bg-surface-container-low border-t border-outline-variant dark:border-outline z-50">
    <a class="flex flex-col items-center justify-center bg-primary-container text-on-primary-container rounded-full px-4 py-1 active:scale-95 transition-transform" href="#dashboard">
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">dashboard</span>
      <span class="font-label-bold text-label-bold mt-1 text-[10px]">{cur.home}</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 active:scale-95 transition-transform" href="#cases">
      <span class="material-symbols-outlined">medical_information</span>
      <span class="font-label-bold text-label-bold mt-1 text-[10px]">{cur.patientRegistry}</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 active:scale-95 transition-transform" href="#reports">
      <span class="material-symbols-outlined">assessment</span>
      <span class="font-label-bold text-label-bold mt-1 text-[10px]">{cur.pendingReports}</span>
    </a>
    <a class="flex flex-col items-center justify-center text-on-surface-variant dark:text-on-surface-variant px-4 py-1 active:scale-95 transition-transform" href="#settings">
      <span class="material-symbols-outlined">settings</span>
      <span class="font-label-bold text-label-bold mt-1 text-[10px]">{cur.help}</span>
    </a>
  </nav>

  <!-- Case Registration Form (Modal / Panel) with strict localization and validation -->
  {#if showForm}
    <div id="case-modal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 transition-all" role="dialog" aria-modal="true">
      <div class="bg-surface-container-lowest border border-outline-variant rounded-2xl max-w-lg w-full p-6 shadow-2xl flex flex-col gap-4 animate-in fade-in zoom-in-95 duration-200">
        <div class="flex justify-between items-center border-b border-outline-variant pb-3">
          <h3 id="modal-title" class="font-headline-sm text-headline-sm font-bold text-primary">{cur.formTitle}</h3>
          <button class="text-on-surface-variant hover:text-on-surface p-1 rounded-full hover:bg-surface-container transition-colors" on:click={handleCancel} aria-label={cur.cancelButton}>
            <span class="material-symbols-outlined">close</span>
          </button>
        </div>

        {#if formSubmittedSuccessfully}
          <div id="success-message" class="bg-primary-container text-white p-4 rounded-xl flex items-center gap-3 font-semibold text-center justify-center">
            <span class="material-symbols-outlined text-lg">check_circle</span>
            <span>{cur.successMsg}</span>
          </div>
        {:else}
          <form on:submit|preventDefault={handleSubmit} class="flex flex-col gap-4">
            <!-- Patient Name field -->
            <div class="flex flex-col gap-1">
              <label for="patient-name" class="font-label-bold text-label-bold text-on-surface-variant">{cur.patientNameLabel} *</label>
              <input
                type="text"
                id="patient-name"
                bind:value={patientName}
                placeholder={cur.patientNamePlaceholder}
                class="w-full h-11 px-4 border {errors.patientName ? 'border-error ring-1 ring-error' : 'border-outline-variant'} rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-surface dark:bg-surface-container-low transition-all text-on-surface font-body-md"
              />
              {#if errors.patientName}
                <span id="name-error" class="text-error text-xs font-semibold flex items-center gap-1 mt-1">
                  <span class="material-symbols-outlined text-sm">error</span>
                  {errors.patientName}
                </span>
              {/if}
            </div>

            <!-- Outbreak Location field -->
            <div class="flex flex-col gap-1">
              <label for="location-name" class="font-label-bold text-label-bold text-on-surface-variant">{cur.locationLabel} *</label>
              <input
                type="text"
                id="location-name"
                bind:value={locationName}
                placeholder={cur.locationPlaceholder}
                class="w-full h-11 px-4 border {errors.locationName ? 'border-error ring-1 ring-error' : 'border-outline-variant'} rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-surface dark:bg-surface-container-low transition-all text-on-surface font-body-md"
              />
              {#if errors.locationName}
                <span id="location-error" class="text-error text-xs font-semibold flex items-center gap-1 mt-1">
                  <span class="material-symbols-outlined text-sm">error</span>
                  {errors.locationName}
                </span>
              {/if}
            </div>

            <!-- Specialty Select field -->
            <div class="flex flex-col gap-1">
              <label for="specialty-select" class="font-label-bold text-label-bold text-on-surface-variant">{cur.specialtyLabel}</label>
              <select
                id="specialty-select"
                bind:value={specialty}
                class="w-full h-11 px-4 border border-outline-variant rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-surface dark:bg-surface-container-low text-on-surface font-body-md"
              >
                <option value="Epidemiology">{strictRussian ? cur.specEpidemiology : "Epidemiology"}</option>
                <option value="Infectious">{strictRussian ? cur.specInfectious : "Infectious Diseases"}</option>
                <option value="Pediatrics">{strictRussian ? cur.specPediatrics : "Pediatrics"}</option>
              </select>
            </div>

            <!-- Severity level -->
            <div class="flex flex-col gap-1">
              <label for="severity-select" class="font-label-bold text-label-bold text-on-surface-variant">{cur.severityLabel}</label>
              <select
                id="severity-select"
                bind:value={severity}
                class="w-full h-11 px-4 border border-outline-variant rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-surface dark:bg-surface-container-low text-on-surface font-body-md"
              >
                <option value="Low">{strictRussian ? cur.sevLow : "Low"}</option>
                <option value="Medium">{strictRussian ? cur.sevMedium : "Medium"}</option>
                <option value="High">{strictRussian ? cur.sevHigh : "High"}</option>
              </select>
            </div>

            <!-- Action buttons -->
            <div class="flex gap-3 justify-end mt-4">
              <button
                type="button"
                id="cancel-btn"
                class="px-5 h-11 bg-transparent border border-outline text-secondary font-label-bold text-label-bold rounded-lg hover:bg-surface-container-low transition-all active:scale-95"
                on:click={handleCancel}
              >
                {cur.cancelButton}
              </button>
              <button
                type="submit"
                id="submit-btn"
                class="px-5 h-11 bg-primary text-on-primary font-label-bold text-label-bold rounded-lg hover:bg-on-primary-fixed-variant transition-all active:scale-95"
              >
                {cur.submitButton}
              </button>
            </div>
          </form>
        {/if}
      </div>
    </div>
  {/if}
</div>

<style>
  :global(body) {
    background-color: #f9f9ff;
    color: #111c2d;
    margin: 0;
    font-family: 'Inter', sans-serif;
  }
</style>