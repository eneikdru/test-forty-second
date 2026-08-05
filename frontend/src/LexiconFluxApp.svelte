<script>
  import LexiconButton from './components/LexiconButton.svelte';
  import LexiconCard from './components/LexiconCard.svelte';
  import LexiconSearchInput from './components/LexiconSearchInput.svelte';

  // State
  let searchQuery = '';
  let selectedCategory = 'all';
  let activeRole = 'ординатор';
  let activeTab = 'overview'; // 'overview' | 'components' | 'documents'
  let alertMessage = '';
  let alertType = 'success';

  // Seed Data: Documents in the Knowledge Base
  const documents = [
    {
      id: 'doc-1',
      title: 'ФГОС ВО Эпидемиология (31.08.35)',
      category: 'нормативные акты',
      type: 'PDF',
      specialty: 'Эпидемиология',
      level: 'ординатура',
      date: '2026-05-12',
      author: 'Минобрнауки РФ',
      summary: 'Федеральный государственный образовательный стандарт высшего образования по специальности Эпидемиология.'
    },
    {
      id: 'doc-2',
      title: 'Рабочая программа дисциплины «Инфекционные болезни»',
      category: 'методические материалы',
      type: 'DOCX',
      specialty: 'Инфекционные болезни',
      level: 'ординатура',
      date: '2026-06-20',
      author: 'Иванова М.П.',
      summary: 'Рабочая программа и тематические планы лекций и практических занятий для ординаторов.'
    },
    {
      id: 'doc-3',
      title: 'Шаблон протокола ГЭК (Государственная Экзаменационная Комиссия)',
      category: 'шаблоны',
      type: 'XLSX',
      specialty: 'Все специальности',
      level: 'аспирантура',
      date: '2026-07-02',
      author: 'Учебный отдел ФБУН',
      summary: 'Форма протокола заседания государственной экзаменационной комиссии ЦНИИ Эпидемиологии.'
    },
    {
      id: 'doc-4',
      title: 'Вопросы к кандидатскому экзамену по специальности 3.2.2. Эпидемиология',
      category: 'вопросы к экзаменам',
      type: 'PDF',
      specialty: 'Эпидемиология',
      level: 'аспирантура',
      date: '2026-04-15',
      author: 'Ученый совет ЦНИИ',
      summary: 'Перечень теоретических вопросов и практических задач для подготовки к кандидатскому минимуму.'
    },
    {
      id: 'doc-5',
      title: 'Рекомендации по оформлению научно-квалификационной работы (диссертации)',
      category: 'методические материалы',
      type: 'PDF',
      specialty: 'Все специальности',
      level: 'аспирантура',
      date: '2026-08-01',
      author: 'Научный отдел',
      summary: 'Методические рекомендации по структуре, объему и правилам оформления диссертаций.'
    },
    {
      id: 'doc-6',
      title: 'Локальный регламент проведения ГИА в ЦНИИ Эпидемиологии',
      category: 'нормативные акты',
      type: 'PDF',
      specialty: 'Все специальности',
      level: 'ординатура',
      date: '2026-03-10',
      author: 'Роспотребнадзор',
      summary: 'Положение об итоговой аттестации обучающихся по программам подготовки кадров высшей квалификации.'
    }
  ];

  // Acronym helper for search matching: "ФБУН" (ЦНИИ Эпидемиологии), "ГЭК", "ГИА", "ФГОС"
  function matchesQuery(doc, query) {
    if (!query) return true;
    const cleanQuery = query.toLowerCase().trim();

    // Exact text matches
    const inTitle = doc.title.toLowerCase().includes(cleanQuery);
    const inSummary = doc.summary.toLowerCase().includes(cleanQuery);
    const inAuthor = doc.author.toLowerCase().includes(cleanQuery);
    const inSpecialty = doc.specialty.toLowerCase().includes(cleanQuery);
    const inLevel = doc.level.toLowerCase().includes(cleanQuery);
    const inCategory = doc.category.toLowerCase().includes(cleanQuery);

    // Acronym expands
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

  // Filtered documents list
  $: filteredDocuments = documents.filter(doc => {
    const categoryMatches = selectedCategory === 'all' || doc.category === selectedCategory;
    const searchMatches = matchesQuery(doc, searchQuery);
    return categoryMatches && searchMatches;
  });

  // Roles permission matrix
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
    alertType = 'success';
    setTimeout(() => {
      alertMessage = '';
    }, 4000);
  }
</script>

<!-- Desktop Side Navigation -->
<aside class="hidden md:flex flex-col h-screen py-lg w-72 rounded-r-none bg-surface-dim border-r border-outline-variant shadow-xl sticky top-0 shrink-0 select-none">
  <div class="px-lg pb-md mb-md border-b border-outline-variant/30">
    <div class="flex items-center gap-2 text-primary">
      <span class="material-symbols-outlined text-headline-lg">terminal</span>
      <h1 class="font-headline-lg text-primary tracking-wide">LEXICON_FLUX</h1>
    </div>
    <span class="font-label-caps text-[10px] text-on-surface-variant/60 block mt-1 uppercase">ЦНИИ Эпидемиологии Роспотребнадзора</span>
  </div>
  <nav class="flex-1 overflow-y-auto flex flex-col gap-1">
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'overview' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'overview'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'overview' ? '1' : '0'};">memory</span>
      Обзор Системы
    </button>
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'documents' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'documents'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'documents' ? '1' : '0'};">menu_book</span>
      База Знаний (ФБУН)
    </button>
    <button
      class="flex items-center gap-sm px-4 py-3 font-body-sm text-body-sm transition-all duration-150 ease-in-out text-left border-0 cursor-pointer {activeTab === 'components' ? 'bg-surface-container-highest text-primary border-l-2 border-primary-fixed-dim' : 'text-on-surface-variant hover:bg-surface-variant/30'}"
      on:click={() => activeTab = 'components'}
    >
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' {activeTab === 'components' ? '1' : '0'};">settings_input_component</span>
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

<!-- Mobile Header -->
<header class="md:hidden flex items-center justify-between px-margin-mobile h-16 w-full top-0 sticky bg-background border-b border-outline-variant/30 z-50">
  <div class="flex items-center gap-2 text-primary">
    <span class="material-symbols-outlined">terminal</span>
    <span class="font-headline-lg-mobile text-headline-lg-mobile text-primary">LEXICON_FLUX</span>
  </div>
  <div class="flex items-center gap-2 text-primary">
    <span class="material-symbols-outlined">sensors</span>
  </div>
</header>

<!-- Main Scrollable Canvas -->
<main class="flex-1 p-margin-mobile md:p-margin-desktop overflow-y-auto pb-24 md:pb-margin-desktop">

  {#if alertMessage}
    <div class="fixed top-4 right-4 z-50 bg-surface-container-high rounded-xl ghost-border p-md flex items-center gap-sm shadow-2xl transition-all max-w-sm animate-bounce" role="alert">
      <span class="material-symbols-outlined text-primary-container">info</span>
      <p class="font-body-sm text-body-sm text-on-surface">{alertMessage}</p>
    </div>
  {/if}

  {#if activeTab === 'overview'}
    <!-- OVERVIEW TAB -->
    <header class="mb-xl">
      <h1 class="font-headline-xl text-headline-xl text-primary mb-xs">База Знаний ЦНИИ Эпидемиологии</h1>
      <p class="font-body-md text-body-md text-on-surface-variant max-w-3xl">
        Информационная система и библиотека компонентов на базе дизайн-системы Lexicon Flux. Обеспечивает мгновенный доступ к методическим и нормативным материалам Роспотребнадзора.
      </p>
    </header>

    <!-- Bento Grid (Scales between mobile 1-column to desktop 12-column) -->
    <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter md:gap-md">

      <!-- Stats / Indicator panel (col-span-4) -->
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

      <!-- Search & Mockup Sandbox (col-span-8) -->
      <section class="col-span-1 md:col-span-8 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">Быстрый поиск по базе знаний</h2>
        <div class="flex flex-col gap-md">
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Попробуйте ввести аббревиатуры для автоматической расшифровки: <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ФГОС</code>, <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ГЭК</code>, <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ГИА</code> или <code class="text-primary-fixed bg-surface-variant px-1 rounded font-code-sm">ФБУН</code>.
          </p>
          <LexiconSearchInput bind:value={searchQuery} placeholder="Поиск материалов..." ariaLabel="Интеллектуальный поиск" />
          <div class="flex gap-sm flex-wrap">
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ФГОС'; activeTab = 'documents'; }}>Поиск "ФГОС"</LexiconButton>
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ГЭК'; activeTab = 'documents'; }}>Поиск "ГЭК"</LexiconButton>
            <LexiconButton variant="outline" on:click={() => { searchQuery = 'ГИА'; activeTab = 'documents'; }}>Поиск "ГИА"</LexiconButton>
          </div>
        </div>
      </section>

      <!-- Roles & Permissions Matrix (col-span-12) -->
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

    </div>

  {:else if activeTab === 'documents'}
    <!-- DOCUMENTS TAB -->
    <header class="mb-xl">
      <div class="flex items-center gap-sm mb-xs">
        <h1 class="font-headline-xl text-headline-xl text-primary">Материалы Базы Знаний</h1>
        <span class="font-label-caps text-label-caps bg-surface-variant px-2 py-1 rounded text-primary-fixed">{filteredDocuments.length} док.</span>
      </div>
      <p class="font-body-md text-body-md text-on-surface-variant">Регламенты, ФОСы, шаблоны протоколов ГЭК и учебные материалы ординатуры и аспирантуры.</p>
    </header>

    <!-- Search / Filter Area -->
    <div class="flex flex-col md:flex-row gap-md mb-lg">
      <div class="flex-1">
        <LexiconSearchInput bind:value={searchQuery} placeholder="Фильтр по названию, ключевым словам..." />
      </div>
      <div class="flex gap-xs overflow-x-auto pb-1 shrink-0">
        {#each ['all', 'нормативные акты', 'методические материалы', 'шаблоны', 'вопросы к экзаменам'] as cat}
          <button
            type="button"
            class="font-label-caps text-[11px] px-md py-2 rounded border uppercase cursor-pointer whitespace-nowrap transition-all {selectedCategory === cat ? 'bg-secondary text-on-secondary border-secondary' : 'bg-surface-container text-on-surface-variant border-outline-variant hover:border-outline'}"
            on:click={() => selectedCategory = cat}
          >
            {cat === 'all' ? 'Все разделы' : cat}
          </button>
        {/each}
      </div>
    </div>

    <!-- Documents Responsive List (4 to 12 column cards representation) -->
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

  {:else if activeTab === 'components'}
    <!-- COMPONENTS DEMO TAB -->
    <header class="mb-xl">
      <h1 class="font-headline-xl text-headline-xl text-primary mb-xs">Библиотека Компонентов Lexicon Flux</h1>
      <p class="font-body-md text-body-md text-on-surface-variant max-w-3xl">Каталог переиспользуемых UI-компонентов для построения консистентных интерфейсов.</p>
    </header>

    <div class="grid grid-cols-1 md:grid-cols-12 gap-gutter md:gap-md">
      <!-- Buttons section -->
      <section class="col-span-1 md:col-span-12 bg-surface-container rounded-xl ghost-border p-lg">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-lg">LexiconButton (Кнопки)</h2>
        <div class="flex flex-col gap-md">
          <div class="flex flex-wrap gap-md items-center">
            <LexiconButton variant="primary" on:click={() => triggerAction('Нажата Primary Кнопка')}>Primary Button</LexiconButton>
            <LexiconButton variant="secondary" on:click={() => triggerAction('Нажата Secondary Кнопка')}>Secondary Button</LexiconButton>
            <LexiconButton variant="outline" on:click={() => triggerAction('Нажата Outline Кнопка')}>Outline Button</LexiconButton>
            <LexiconButton variant="danger" on:click={() => triggerAction('Нажата Danger Кнопка')}>Danger Button</LexiconButton>
            <LexiconButton variant="primary" disabled>Disabled Button</LexiconButton>
          </div>
          <div class="bg-surface-container-high rounded-lg p-md mt-sm border border-outline-variant/20">
            <h3 class="font-label-caps text-label-caps text-primary mb-xs">Параметры (Props):</h3>
            <ul class="font-code-sm text-code-sm text-on-surface-variant flex flex-col gap-xs">
              <li><code class="text-primary-fixed">variant</code>: "primary" | "secondary" | "outline" | "danger"</li>
              <li><code class="text-primary-fixed">disabled</code>: boolean (отключение интерактивности)</li>
              <li><code class="text-primary-fixed">type</code>: "button" | "submit" | "reset"</li>
            </ul>
          </div>
        </div>
      </section>

      <!-- Cards section -->
      <section class="col-span-1 md:col-span-6 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">LexiconCard (Карточки)</h2>
        <LexiconCard title="Пример карточки" subtitle="Подзаголовок или категория" interactive={true}>
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Карточки в Lexicon Flux поддерживают интерактивный ховер, имеют закругление углов (xl) и изящную границу (.ghost-border).
          </p>
          <div slot="footer">
            <LexiconButton variant="outline" on:click={() => triggerAction('Действие в карточке')}>Действие</LexiconButton>
          </div>
        </LexiconCard>
      </section>

      <!-- Search inputs section -->
      <section class="col-span-1 md:col-span-6 bg-surface-container rounded-xl ghost-border p-lg flex flex-col gap-md">
        <h2 class="font-label-caps text-label-caps text-on-surface-variant border-b border-outline-variant/30 pb-xs mb-sm">LexiconSearchInput (Поиск)</h2>
        <div class="flex flex-col gap-md">
          <p class="font-body-sm text-body-sm text-on-surface-variant">
            Инпут поиска с иконкой лупы и кнопкой очистки, реагирует на фокус изменением цвета рамки на яркий неоновый.
          </p>
          <LexiconSearchInput placeholder="Тестовый поиск..." />
        </div>
      </section>

    </div>
  {/if}

</main>

<!-- Mobile Bottom Nav Bar -->
<nav class="md:hidden flex justify-around items-center px-gutter w-full fixed bottom-0 z-50 h-16 bg-surface-container-low border-t border-outline-variant/20">
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'overview' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'overview'}
    aria-label="Обзор системы"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'overview' ? '1' : '0'};">dashboard</span>
  </button>
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'documents' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'documents'}
    aria-label="Материалы базы знаний"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'documents' ? '1' : '0'};">menu_book</span>
  </button>
  <button
    type="button"
    class="flex flex-col items-center justify-center p-2 rounded-xl transition-all active:scale-90 duration-200 border-0 bg-transparent {activeTab === 'components' ? 'text-secondary-fixed-dim bg-secondary-container/20' : 'text-outline hover:text-primary'}"
    on:click={() => activeTab = 'components'}
    aria-label="Библиотека компонентов"
  >
    <span class="material-symbols-outlined font-label-caps text-label-caps" style="font-variation-settings: 'FILL' {activeTab === 'components' ? '1' : '0'};">settings_input_component</span>
  </button>
</nav>

<style>
  /* Custom bounce animate and general transition overrides */
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
