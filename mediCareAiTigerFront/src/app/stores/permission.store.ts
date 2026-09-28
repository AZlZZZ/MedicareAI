import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';

interface PermissionState {
  permissions: any[];
  loading: boolean;
  role: string;
  userType: string;
  id: number;
  firstName: string;
  lastName: string;
  gender: string;
}

export const PermissionStore = signalStore(
  { providedIn: 'root' },
  withState<PermissionState>({
    permissions: [],
    loading: false,
    role: '',
    userType: '',
    id: 0,
    firstName: '',
    lastName: '',
    gender: '',
  }),
  withMethods((store) => ({
    setMPermissions: (permissions: any[]) => {
      patchState(store, { permissions });
    },
    setRole: (role: string) => {
      patchState(store, { role });
    },
    setId: (id: number) => {
      patchState(store, { id });
    },
    setFirstName: (firstName: string) => {
      patchState(store, { firstName });
    },
    setLastName: (lastName: string) => {
      patchState(store, { lastName });
    },
    setGender: (gender: string) => {
      patchState(store, { gender });
    },
    setData: (id: number, lastName: string, firstName: string, role: string, userType: string) => {
      patchState(store, { id, lastName, firstName, role, userType, loading: false });
    },
    checkPermission: (feature: string, subFeature: any): boolean => {
      return store.permissions().some(
        (p: any) =>
          p.name === feature &&
          p.subFunctionRespDTOS.some((sub: any) => sub.name === subFeature)
      );
    },
  }))
);
