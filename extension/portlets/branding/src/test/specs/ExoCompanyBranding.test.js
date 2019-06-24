import { createLocalVue, shallowMount } from '@vue/test-utils';
import fetchMock from 'fetch-mock';
import flushPromises from 'flush-promises';
import ExoCompanyBranding from '../../main/webapp/company-branding-app/components/ExoCompanyBranding';

const localVue = createLocalVue();

describe('ExoCompanyBranding.test.js', () => {
  let cmp;

  beforeEach(() => {
    fetchMock.get('/rest/v1/platform/branding', {
      'companyName': 'Default Company Name',
      'topBarTheme': 'Dark'
    });
    fetchMock.put('/rest/v1/platform/branding', 200);

    Object.defineProperty(document.location, 'reload', {
      configurable: true
    });

    cmp = shallowMount(ExoCompanyBranding, {
      localVue,
      mocks: {
        $t: () => {}
      }
    });
  });

  it('should display default company branding information in form', () => {
    // Given
    cmp.vm.branding.companyName = 'My Company';
    cmp.vm.branding.topBarTheme = 'Light';

    // When
    const companyNameInput = cmp.find('#companyNameInput');
    const topBarThemeLightInput = cmp.find('#navigationStyle').find('input[value=Light]');
    const topBarThemeDarkInput = cmp.find('#navigationStyle').find('input[value=Dark]');

    // Then
    expect(companyNameInput.element.value).toBe('My Company');
    expect(topBarThemeLightInput.element.checked).toBe(true);
    expect(topBarThemeDarkInput.element.checked).toBe(false);
  });

  it('should reset form value when clicking on Cancel button', done => {
    // Given
    cmp.vm.branding.companyName = 'My Company';
    cmp.vm.branding.topBarTheme = 'Light';

    // When
    cmp.find('#cancel').trigger('click');

    flushPromises().then(() => {
      // Then
      expect(cmp.vm.branding.companyName).toBe('Default Company Name');
      expect(cmp.vm.branding.topBarTheme).toBe('Dark');
      expect(cmp.find('#savenotok').attributes().style).toBe('display: none;');
      expect(cmp.find('#saveinfo').attributes().style).toBe('display: none;');
      expect(cmp.find('#cancelinfo').attributes().style).toBe('display: block;');
      expect(cmp.find('#mustpng').attributes().style).toBe('display: none;');
      done();
    });
  });

  it('should display error message when saving with a non PNG image', () => {
    // Given
    document.location.reload = jest.fn();

    cmp.vm.branding.logo.uploadId = 123456;
    cmp.vm.branding.logo.name = 'logo.jpg';

    // When
    cmp.find('#save').trigger('click');

    // Then
    expect(document.location.reload).not.toHaveBeenCalled();
    expect(cmp.vm.branding.logo.uploadId).toBe(null);
    expect(cmp.find('#savenotok').attributes().style).toBe('display: none;');
    expect(cmp.find('#saveinfo').attributes().style).toBe('display: none;');
    expect(cmp.find('#cancelinfo').attributes().style).toBe('display: none;');
    expect(cmp.find('#mustpng').attributes().style).toBe('display: block;');

    document.location.reload.mockRestore();
  });

  it('should save form data when clicking on Save button', done => {
    // Given
    document.location.reload = jest.fn();

    cmp.vm.branding.companyName = 'My New Company';
    cmp.vm.branding.topBarTheme = 'Light';

    // When
    cmp.find('#save').trigger('click');

    flushPromises().then(() => {
      // Then
      expect(document.location.reload).toHaveBeenCalled();

      expect(cmp.find('#savenotok').attributes().style).toBe('display: none;');
      expect(cmp.find('#saveinfo').attributes().style).toBe('display: none;');
      expect(cmp.find('#cancelinfo').attributes().style).toBe('display: none;');
      expect(cmp.find('#mustpng').attributes().style).toBe('display: none;');

      done();

      document.location.reload.mockRestore();
    });
  });

  afterEach(() => {
    fetchMock.restore();
  });
});