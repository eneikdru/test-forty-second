<script>
  import LexiconButton from './components/LexiconButton.svelte';
  import LexiconCard from './components/LexiconCard.svelte';
  import LexiconSearchInput from './components/LexiconSearchInput.svelte';

  // Состояние (State)
  let searchQuery = '';
  let selectedCategory = 'все';
  let activeRole = 'ординатор';
  let activeTab = 'обзор'; // 'обзор' | 'компоненты' | 'документы'
  let alertMessage = '';
  let alertType = 'успех';

  // Переменные формы запроса (Form variables)
  let applicantName = '';
  let applicantEmail = '';
  let documentTitle = '';
  let updateDescription = '';

  let formError = '';
  let formSuccess = '';

  // Исходные данные: Документы в базе знаний
  const documents = [
    {
      id: 'док-1',
      title: 'ФГОС ВО Эпидемиология (31.08.35)',
      category: 'нормативные акты',
      type: 'ПДФ',
      specialty: 'Эпидемиология',
      level: 'ординатура',
      date: '2026-05-12',
      author: 'Минобрнауки РФ',
      summary: 'Федеральный государственный образовательный стандарт высшего образования по специальности Эпидемиология.'
    },
    {
      id: 'док-2',
      title: 'Рабочая программа дисциплины «Инфекционные болезни»',
      category: 'методические материалы',
      type: 'ДОКС',
      specialty: 'Инфекционные болезни',
      level: 'ординатура',
      date: '2026-06-20',
      author: 'Иванова М.П.',
      summary: 'Рабочая программа и тематические планы лекций и практических занятий для ординаторов.'
    },
    {
      id: 'док-3',
      title: 'Шаблон протокола ГЭК (Государственная Экзаменационная Комиссия)',
      category: 'шаблоны',
      type: 'ЭКСЛ',
      specialty: 'Все специальности',
      level: 'аспирантура',
      date: '2026-07-02',
      author: 'Учебный отдел ФБУН',
      summary: 'Форма протокола заседания государственной экзаменационной комиссии ЦНИИ Эпидемиологии.'
    },
    {
      id: 'док-4',
      title: 'Вопросы к кандидатскому экзамену по специальности 3.2.2. Эпидемиология',
      category: 'вопросы к экзаменам',
      type: 'ПДФ',
      specialty: 'Эпидемиология',
      level: 'аспирантура',
      date: '2026-04-15',
      author: 'Ученый совет ЦНИИ',
      summary: 'Перечень теоретических вопросов и практических задач для подготовки к кандидатскому минимуму.'
    },
    {
      id: 'док-5',
      title: 'Рекомендации по оформлению научно-квалификационной работы (диссертации)',
      category: 'методические материалы',
      type: 'ПДФ',
      specialty: 'Все специальности',
      level: 'аспирантура',
      date: '2026-08-01',
      author: 'Научный отдел',
      summary: 'Методические рекомендации по структуре, объему и правилам оформления диссертаций.'
    },
    {
      id: 'док-6',
      title: 'Локальный регламент проведения ГИА в ЦНИИ Эпидемиологии',
      category: 'нормативные акты',
      type: 'ПДФ',
      specialty: 'Все специальности',
      level: 'ординатура',
      date: '2026-03-10',
      author: 'Роспотребнадзор',
      summary: 'Положение об итоговой аттестации обучающихся по программам подготовки кадров высшей квалификации.'
    }
  ];

  // Вспомогательная функция для поиска аббревиатур: "ФБУН", "ГЭК", "ГИА", "ФГОС"
  function matchesQuery(doc, query) {
    if (!query) return true;
    const cleanQuery = query.toLowerCase().trim();

    // Точные совпадения текста
    const inTitle = doc.title.toLowerCase().includes(cleanQuery);
    const inSummary = doc.summary.toLowerCase().includes(cleanQuery);
    const inAuthor = doc.author.toLowerCase().includes(cleanQuery);
    const inSpecialty = doc.specialty.toLowerCase().includes(cleanQuery);
    const inLevel = doc.level.toLowerCase().includes(cleanQuery);
    const inCategory = doc.category.toLowerCase().includes(cleanQuery);

    // Раскрытие аббревиатур
    let acronymMatch = false;
    if (cleanQuery.includes('фбун') && (doc.title.includes('ЦНИИ') || doc.summary.includes('ЦНИИ') || doc.author.includes('Учебный') || doc.author.includes('Ученый'))) {
      acronymMatch = true;
    }
    if (cleanQuery.includes('гэк') && (doc.title.includes('ГЭК') || doc.title.includes('Экзаменационная') || doc.summary.includes('экзамен'))) {
      acronymMatch = true;
    }
    if (cleanQuery.includes('гиа') && (doc.title.includes('ГИА') || doc.summary.includes('аттестация') || doc.title.includes('аттестация'))) {
      acronymMatch = true;
    }
    if (cleanQuery.includes('фгос') && (doc.title.includes('ФГОС') || doc.title.includes('стандарт') || doc.summary.includes('стандарт'))) {
      acronymMatch = true;
    }

    return inTitle || inSummary || inAuthor || inSpecialty || inLevel || inCategory || acronymMatch;
  }

  // Список отфильтрованных документов
  $: filteredDocuments = documents.filter(doc => {
    const categoryMatches = selectedCategory === 'все' || doc.category === selectedCategory;
    const searchMatches = matchesQuery(doc, searchQuery);
    return categoryMatches && searchMatches;
  });

  // Матрица ролей и полномочий
  const rolePermissions = {
    'администратор': {
      title: 'Администратор Базы Знаний',
      desc: 'Полный доступ к системе управления, аудит действий пользователей и конфигурация.',
      rights: ['Создание и удаление разделов', 'Управление пользователями и правами', 'Модерация всего контента', 'Выгрузка отчетов и резервное копирование']
    },
    'контент-менеджер': {
      title: 'Контент-Менеджер',
      desc: 'Отвечает за наполнение, категоризацию, тегирование и актуализацию материалов.',
      rights: ['Добавление и редактирование статей', 'Классификация и тегирование документов', 'Контроль актуальности версий', 'Назначение ответственных за разделы']
    },
    'преподаватель': {
      title: 'Преподаватель / Научный руководитель',
      desc: 'Доступ к материалам, создание методических подборок и предложение правок.',
      rights: ['Просмотр всех материалов', 'Загрузка методических материалов', 'Формирование подборок для учебных групп', 'Предложение правок к документам']
    },
    'ординатор': {
      title: 'Ординатор / Аспирант / Слушатель',
      desc: 'Доступ к учебным материалам, шаблонам документов и быстрый интеллектуальный поиск.',
      rights: ['Просмотр и скачивание файлов', 'Поиск по ключевым словам и аббревиатурам', 'Доступ к образцам и шаблонам ГЭК', 'Подписка на обновления материалов']
    }
  };

  function triggerAction(actionName, docTitle = '') {
    alertMessage = docTitle
      ? `Выполнено действие: "${actionName}" для документа "${docTitle}"`
      : `Выполнено действие: "${actionName}" в роли "${rolePermissions[activeRole].title}"`;
    alertType = 'успех';
    setTimeout(() => {
      alertMessage = '';
    }, 4000);
  }

  function handleFormSubmit(event) {
    event.preventDefault();
    formError = '';
    formSuccess = '';

    // Валидация ФИО
    const nameTrimmed = applicantName.trim();
    if (!nameTrimmed) {
      formError = 'Ошибка проверки: Пожалуйста, укажите ФИО заявителя.';
      return;
    }
    const cyrillicRegex = /^[а-яА-ЯёЁ\s\-]+$/;
    if (!cyrillicRegex.test(nameTrimmed)) {
      formError = 'Ошибка проверки: ФИО должно содержать только символы кириллицы, пробелы и дефисы.';
      return;
    }

    // Валидация Почты
    const emailTrimmed = applicantEmail.trim();
    if (!emailTrimmed) {
      formError = 'Ошибка проверки: Пожалуйста, укажите адрес электронной почты.';
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailTrimmed)) {
      formError = 'Ошибка проверки: Некорректный формат адреса электронной почты.';
      return;
    }

    // Валидация Названия документа
    const docTitleTrimmed = documentTitle.trim();
    if (!docTitleTrimmed) {
      formError = 'Ошибка проверки: Пожалуйста, введите название документа.';
      return;
    }
    if (docTitleTrimmed.length < 5) {
      formError = 'Ошибка проверки: Название документа должно содержать не менее пяти символов.';
      return;
    }

    // Валидация Описания изменений
    const descTrimmed = updateDescription.trim();
    if (!descTrimmed) {
      formError = 'Ошибка проверки: Пожалуйста, введите описание необходимых изменений.';
      return;
    }
    if (descTrimmed.length < 10) {
      formError = 'Ошибка проверки: Описание должно содержать не менее десяти символов.';
      return;
    }

    // Успех
    formSuccess = 'Успех: Ваш запрос на актуализацию документов успешно зарегистрирован и отправлен в учебный отдел!';
    applicantName = '';
    applicantEmail = '';
    documentTitle = '';
    updateDescription = '';
  }
</script>

<!-- Боковая навигация для десктопа -->
<aside class="hidden md:flex flex-col h-screen py-lg w-72 rounded-r-none bg-surface-dim border-r border-outline-variant shadow-xl sticky top-0 shrink-0 select-none">
  <div class="px-lg pb-md mb-md border-b border-outline-variant/30">
    <div class="flex items-center gap-2 text-primary">
      <span class="material-symbols-outlined text-headline-lg">terminal</span>
      <h1 class="font-headline-lg text-primary tracking-wide">ЛЕКСИКОН_ФЛАКС</h1>
    </div>
    <span class="font-label-caps text-[10px] text-on-surface-variant/60 block mt-1 uppercase">ЦНИИ Эпидемиологии Роспотребнадзора</span>
  </div>
  <nav class="flex-1 overflow-y-auto flex flex-col gap-1">
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'обзор' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'обзор'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'обзор' ? '1' : '0'};">memory</span>
      Обзор Системы
    </button>
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'документы' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'документы'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'документы' ? '1' : '0'};">menu_book</span>
      База Знаний (ФБУН)
    </button>
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'компоненты' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'компоненты'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'компоненты' ? '1' : '0'};">settings_input_component</span>
      Библиотека Компонентов
    </button>
  </nav>
  <div class="px-lg pt-md mt-auto border-t border-outline-variant/30 text-center">
    <div class="flex items-center justify-center gap-sm">
      <span class="w-2 h-2 rounded-full bg-primary-container shadow-[0_0_6px_rgba(0,229,255,0.6)]"></span>
      <span class="font-label-caps text-label-caps text-on-surface-variant">База знаний v1.0.0</span>
    </div>
  </div>
</aside>

<!-- Мобильный заголовок -->
<header class="md:hidden flex items-center justify-between px-margin-mobile h-16 w-full top-0 sticky bg-background border-b border-outline-variant/30 z-50">
  <div class="flex items-center gap-2 text-primary">
    <span class="material-symbols-outlined">terminal</span>
    <span class="font-headline-lg-mobile text-headline-lg-mobile text-primary">ЛЕКСИКОН_ФЛАКС</span>
  </div>
  <div class="flex items-center gap-2 text-primary">
    <span class="material-symbols-outlined">sensors</span>
  </div>
</header>

<!-- Главный холст -->
<main class="flex-1 p-margin-mobile md:p-margin-desktop overflow-y-auto pb-24 md:pb-margin-desktop">

  {#if alertMessage}
    <div class="fixed top-4 right-4 z-50 bg-surface-container-high rounded-xl ghost-border p-md flex items-center gap-sm shadow-2xl transition-all max-w-sm animate-bounce" role="alert">
      <span class="material-symbols-outlined text-primary-container">info</span>
      <p class="font-body-sm text-body-sm text-on-surface">{alertMessage}</p>
    </div>
  {/if}

  {#if activeTab === 'обзор'}
    <!-- ВКЛАДКА ОБЗОРА -->
    <header class="mb-xl">
      <h1 class="font-headline-xl text-headline-xl text-primary mb-xs">База Знаний ЦНИИ Эпидемиологии</h1>
      <p class="font-body-md text-body-md text-on-surface-variant max-w-3xl">
        Информационная система и библиотека компонентов на базе дизайн-системы Лексикон Флакс. Обеспечивает мгновенный доступ к методическим и нормативным материалам Роспотребнадзора.
      </p>
    </header>

    <!-- Bento Grid -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter md:gap-md">

      <!-- Панель мониторинга -->
      <section class="col-span-1 md:col-span-4 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Статус и Мониторинг</h2>
        <div class="flex flex-col gap-sm justify-center flex-1">
          <div class="flex items-center gap-sm">
            <span class="w-3 h-3 rounded-full bg-primary-container shadow-[0_0_8px_rgba(0,229,255,0.6)]" aria-hidden="true"></span>
            <span class="font-body-sm text-body-sm text-on-surface">База Знаний Активна (ФБУН)</span>
          </div>
          <div class="flex items-center gap-sm">
            <span class="w-3 h-3 rounded-full bg-secondary shadow-[0_0_8px_rgba(224,182,255,0.6)]" aria-hidden="true"></span>
            <span class="font-body-sm text-body-sm text-on-surface">Синхронизация с ФГОС 3+</span>
          </div>
          <div class="flex items-center gap-sm">
            <span class="w-3 h-3 rounded-full bg-error shadow-[0_0_8px_rgba(255,180,171,0.6)]" aria-hidden="true"></span>
            <span class="font-body-sm text-body-sm text-on-surface">0 неактуальных версий</span>
          </div>
        </div>
      </section>

      <!-- Поиск и интерактивная панель -->
      <section class="col-span-1 md:col-span-8 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Быстрый поиск по базе знаний</h2>
        <div class="flex flex-col gap-md">
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Попробуйте ввести аббревиатуры для автоматической расшифровки: <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ФГОС</code>, <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ГЭК</code>, <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ГИА</code> или <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ФБУН</code>.
          </p>
          <LexiconSearchInput bind:value={searchQuery} placeholder="Поиск материалов..." ariaLabel="Интеллектуальный поиск" />
          <div class="flex gap-sm flex-wrap">
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ФГОС'; activeTab = 'документы'; }}>Поиск "ФГОС"</LexiconButton>
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ГЭК'; activeTab = 'документы'; }}>Поиск "ГЭК"</LexiconButton>
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ГИА'; activeTab = 'документы'; }}>Поиск "ГИА"</LexiconButton>
          </div>
        </div>
      </section>

      <!-- Матрица ролей -->
      <section class="col-span-1 md:col-span-12 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <header class="border-b border-outline-variant/30 pb-xs mb-sm flex flex-col sm:flex-row sm:items-center justify-between gap-sm">
          <h2 class="font-label-caps text-label-caps text-on-surface-variant">Матрица ролей и полномочий</h2>
          <div class="flex items-center gap-xs flex-wrap">
            {#each Object.keys(rolePermissions) as rKey}
              <button
                type="button"
                class="font-label-caps text-[11px] px-md py-1 rounded border uppercase cursor-pointer transition-all {activeRole === rKey ? 'bg-primary text-on-primary border-primary' : 'bg-transparent text-on-surface-variant border-outline-variant hover:border-outline'}"
                on:click={() => activeRole = rKey}
              >
                {rKey}
              </button>
            {/each}
          </div>
        </header>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-lg items-center">
          <div>
            <h3 class="font-headline-lg-mobile text-headline-lg-mobile text-primary mb-xs">{rolePermissions[activeRole].title}</h3>
            <p class="font-body-md text-body-md text-on-surface-variant mb-md">{rolePermissions[activeRole].desc}</p>
            <LexiconButton variant="primary" on:click={() => triggerAction('Активировать права')}>
              Проверить полномочия
            </LexiconButton>
          </div>
          <div class="bg-surface-container-high rounded-xl ghost-border p-md">
            <h4 class="font-label-caps text-label-caps text-primary mb-sm">Права и возможности роли:</h4>
            <ul class="flex flex-col gap-xs">
              {#each rolePermissions[activeRole].rights as right}
                <li class="flex items-center gap-sm font-body-sm text-body-sm text-on-surface">
                  <span class="material-symbols-outlined text-primary text-[16px]">verified</span>
                  {right}
                </li>
              {/each}
            </ul>
          </div>
        </div>
      </section>

      <!-- Форма запроса на актуализацию (валидация на русском) -->
      <section class="col-span-1 md:col-span-12 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Запрос на актуализацию учебно-методических материалов</h2>
        <form on:submit={handleFormSubmit} class="flex flex-col gap-md w-full">
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            В соответствии с санитарно-эпидемиологическими правилами, все материалы должны проходить регулярную проверку. Если вы обнаружили неактуальные сведения, отправьте запрос на актуализацию через форму ниже.
          </p>

          {#if formError}
            <div class="p-md bg-error-container text-on-error-container border border-error/20 rounded-lg flex items-center gap-sm" role="alert">
              <span class="material-symbols-outlined text-[20px]">warning</span>
              <span class="font-body-sm text-body-sm font-semibold">{formError}</span>
            </div>
          {/if}

          {#if formSuccess}
            <div class="p-md bg-primary-container text-on-primary-container border border-primary/20 rounded-lg flex items-center gap-sm" role="alert">
              <span class="material-symbols-outlined text-[20px]">check_circle</span>
              <span class="font-body-sm text-body-sm font-semibold">{formSuccess}</span>
            </div>
          {/if}

          <div class="grid grid-cols-1 md:grid-cols-2 gap-md w-full">
            <div class="flex flex-col gap-xs w-full">
              <label for="applicant-name" class="font-label-caps text-[11px] text-on-surface-variant uppercase">ФИО заявителя (только кириллица)</label>
              <input
                id="applicant-name"
                type="text"
                bind:value={applicantName}
                placeholder="Иванов Иван Иванович"
                class="bg-surface-container-high border border-outline-variant rounded-lg p-sm font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary w-full"
              />
            </div>

            <div class="flex flex-col gap-xs w-full">
              <label for="applicant-email" class="font-label-caps text-[11px] text-on-surface-variant uppercase">Электронная почта</label>
              <input
                id="applicant-email"
                type="text"
                bind:value={applicantEmail}
                placeholder="primer@pochta.ru"
                class="bg-surface-container-high border border-outline-variant rounded-lg p-sm font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary w-full"
              />
            </div>
          </div>

          <div class="flex flex-col gap-xs w-full">
            <label for="doc-title" class="font-label-caps text-[11px] text-on-surface-variant uppercase">Название документа</label>
            <input
              id="doc-title"
              type="text"
              bind:value={documentTitle}
              placeholder="Введите название учебно-методического материала"
              class="bg-surface-container-high border border-outline-variant rounded-lg p-sm font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary w-full"
            />
          </div>

          <div class="flex flex-col gap-xs w-full">
            <label for="update-desc" class="font-label-caps text-[11px] text-on-surface-variant uppercase">Описание необходимых изменений</label>
            <textarea
              id="update-desc"
              bind:value={updateDescription}
              placeholder="Опишите, какие разделы требуют актуализации и почему..."
              rows="3"
              class="bg-surface-container-high border border-outline-variant rounded-lg p-sm font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary resize-none w-full"
            ></textarea>
          </div>

          <div class="flex justify-end mt-sm">
            <LexiconButton type="submit" variant="primary">Отправить запрос на актуализацию</LexiconButton>
          </div>
        </form>
      </section>

    </div>

  {:else if activeTab === 'документы'}
    <!-- ВКЛАДКА ДОКУМЕНТОВ -->
    <header class="mb-xl">
      <div class="flex items-center gap-sm mb-xs">
        <h1 class="font-headline-xl text-headline-xl text-primary">Материалы Базы Знаний</h1>
        <span class="font-label-caps text-label-caps bg-surface-variant px-2 py-1 rounded text-primary-fixed">{filteredDocuments.length} док.</span>
      </div>
      <p class="font-body-md text-body-md text-on-surface-variant">Регламенты, ФОСы, шаблоны протоколов ГЭК и учебные материалы ординатуры и аспирантуры.</p>
    </header>

    <!-- Поиск и фильтры -->
    <div class="flex flex-col md:flex-row gap-md mb-lg">
      <div class="flex-1">
        <LexiconSearchInput bind:value={searchQuery} placeholder="Фильтр по названию, ключевым словам..." />
      </div>
      <div class="flex gap-xs overflow-x-auto pb-1 shrink-0">
        {#each ['все', 'нормативные акты', 'методические материалы', 'шаблоны', 'вопросы к экзаменам'] as cat}
          <button
            type="button"
            class="font-label-caps text-[11px] px-md py-2 rounded border uppercase cursor-pointer whitespace-nowrap transition-all {selectedCategory === cat ? 'bg-secondary text-on-secondary border-secondary' : 'bg-surface-container text-on-surface-variant border-outline-variant hover:border-outline'}"
            on:click={() => selectedCategory = cat}
          >
            {cat === 'все' ? 'Все разделы' : cat}
          </button>
        {/each}
      </div>
    </div>

    <!-- Список документов -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-md">
      {#each filteredDocuments as doc}
        <LexiconCard title={doc.title} subtitle={`${doc.specialty} • ${doc.level}`}>
          <p class="text-body-sm text-on-surface-variant mb-md flex-1">{doc.summary}</p>
          <div class="flex items-center justify-between font-code-sm text-code-sm border-t border-outline-variant/10 pt-sm mt-auto">
            <span class="bg-surface-variant text-on-surface-variant px-2 py-0.5 rounded text-[11px] uppercase">{doc.type}</span>
            <span class="text-on-surface-variant/70">{doc.date}</span>
          </div>
          <div slot="footer" class="w-full flex justify-between items-center">
            <span class="font-body-sm text-[12px] text-on-surface-variant/80">Автор: {doc.author}</span>
            <LexiconButton variant="outline" on:click={() => triggerAction('Скачать файл', doc.title)}>Скачать</LexiconButton>
          </div>
        </LexiconCard>
      {/each}
      {#if filteredDocuments.length === 0}
        <div class="col-span-full py-xl text-center bg-surface-container rounded-xl ghost-border">
          <span class="material-symbols-outlined text-[48px] text-on-surface-variant/40 mb-sm">folder_open</span>
          <p class="font-body-md text-body-md text-on-surface-variant">Документы не найдены по запросу "{searchQuery}"</p>
        </div>
      {/if}
    </div>

  {:else if activeTab === 'компоненты'}
    <!-- ВКЛАДКА БИБЛИОТЕКИ КОМПОНЕНТОВ -->
    <header class="mb-xl">
      <h1 class="font-headline-xl text-headline-xl text-primary mb-xs">Библиотека Компонентов Лексикон Флакс</h1>
      <p class="font-body-md text-body-md text-on-surface-variant max-w-3xl">Каталог переиспользуемых UI-компонентов для построения консистентных интерфейсов.</p>
    </header>

    <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter md:gap-md">
      <!-- Кнопки -->
      <section class="col-span-1 md:col-span-12 bg-surface-container rounded-xl ghost-border p-lg">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-lg">Кнопки (LexiconButton)</h2>
        <div class="flex flex-col gap-md">
          <div class="flex flex-wrap gap-md items-center">
            <LexiconButton variant="primary" on:click={() => triggerAction('Нажата Основная Кнопка')}>Основная кнопка</LexiconButton>
            <LexiconButton variant="secondary" on:click={() => triggerAction('Нажата Второстепенная Кнопка')}>Второстепенная кнопка</LexiconButton>
            <LexiconButton variant="outline" on:click={() => triggerAction('Нажата Контурная Кнопка')}>Контурная кнопка</LexiconButton>
            <LexiconButton variant="danger" on:click={() => triggerAction('Нажата Опасная Кнопка')}>Опасная кнопка</LexiconButton>
            <LexiconButton variant="primary" disabled>Заблокированная кнопка</LexiconButton>
          </div>
          <div class="bg-surface-container-high rounded-lg p-md mt-sm border border-outline-variant/20">
            <h3 class="font-label-caps text-label-caps text-primary mb-xs">Параметры (Свойства):</h3>
            <ul class="font-code-sm text-code-sm text-on-surface-variant flex flex-col gap-xs">
              <li><code class="text-primary-fixed">variant (вариант)</code>: "primary" (основной) | "secondary" (второстепенный) | "outline" (контурный) | "danger" (опасный)</li>
              <li><code class="text-primary-fixed">disabled (заблокирован)</code>: логическое значение (отключение интерактивности)</li>
              <li><code class="text-primary-fixed">type (тип)</code>: "button" (кнопка) | "submit" (отправка) | "reset" (сброс)</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Карточки -->
      <section class="col-span-1 md:col-span-6 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Карточки (LexiconCard)</h2>
        <LexiconCard title="Пример карточки" subtitle="Подзаголовок или категория" interactive={true}>
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Карточки в Лексикон Флакс поддерживают интерактивное наведение, имеют закругление углов и изящную границу.
          </p>
          <div slot="footer">
            <LexiconButton variant="outline" on:click={() => triggerAction('Действие в карточке')}>Действие</LexiconButton>
          </div>
        </LexiconCard>
      </section>

      <!-- Поля ввода поиска -->
      <section class="col-span-1 md:col-span-6 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Поиск (LexiconSearchInput)</h2>
        <div class="flex flex-col gap-md">
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Поле ввода для поиска с иконкой лупы и кнопкой очистки, реагирует на фокус изменением цвета рамки на яркий неоновый.
          </p>
          <LexiconSearchInput placeholder="Тестовый поиск..." />
        </div>
      </section>

    </div>
  {/if}

</main>

<!-- Мобильное нижнее меню навигации -->
<nav class="md:hidden flex justify-around items-center px-gutter w-full fixed bottom-0 z-50 h-16 bg-surface-container-low border-t border-outline-variant/20">
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'обзор' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'обзор'}
    aria-label="Обзор системы"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'обзор' ? '1' : '0'};">dashboard</span>
  </button>
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'документы' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'документы'}
    aria-label="Материалы базы знаний"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'документы' ? '1' : '0'};">menu_book</span>
  </button>
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'компоненты' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'компоненты'}
    aria-label="Библиотека компонентов"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'компоненты' ? '1' : '0'};">settings_input_component</span>
  </button>
</nav>

<style>
  @keyframes bounce {
    0%, 100% {
      transform: translateY(0);
      animation-timing-function: cubic-bezier(0.8,0,1,1);
    }
    50% {
      transform: translateY(-8px);
      animation-timing-function: cubic-bezier(0,0,0.2,1);
    }
  }
  .animate-bounce {
    animation: bounce 1s infinite;
  }
</style>
